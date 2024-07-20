from __future__ import division, print_function
import numpy as np
np.random.seed(3)
from scipy.signal import butter, lfilter, lfilter_zi, filtfilt, savgol_filter
from sklearn.preprocessing import MinMaxScaler
import random
random.seed(3)
import os
from tensorflow.keras.layers import Conv2D, MaxPooling2D
import scipy.io as sio
import matplotlib.pyplot as plt
import natsort
from scipy import signal
import math
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.backend import squeeze
from kapre.time_frequency import Spectrogram
from kapre.utils import Normalization2D
from kapre.augmentation import AdditiveNoise
from tensorflow.keras.layers import Input, BatchNormalization, AveragePooling2D, Flatten, Dense, Conv1D, Activation, add, AveragePooling1D, Dropout, Permute, concatenate, MaxPooling1D, LSTM, Reshape, GRU
from tensorflow.keras.regularizers import l2
from tensorflow.keras import Model
from tensorflow.keras import optimizers
from tensorflow.keras.utils import plot_model

# Function to create a mid-spectrogram LSTM layer
def mid_spectrogram_LSTM_layer(input_x):
    l2_lambda = .001
    n_dft = 64
    n_hop = 64
    fmin = 0.0
    fmax = 50 / 2
    x = Permute((2, 1))(input_x)
    x = Spectrogram(n_dft=n_dft, n_hop=n_hop, image_data_format='channels_last', return_decibel_spectrogram=True)(x)
    x = Normalization2D(str_axis='batch')(x)
    x = Flatten()(x)
    x = Dense(32, activation="relu", kernel_regularizer=l2(l2_lambda))(x)
    x = BatchNormalization()(x)
    return x

# Function to create a single channel ResNet
def single_channel_resnet(input_shape, num_filters=64, num_res_blocks=2, cnn_per_res=3, kernel_sizes=[8, 5, 3], max_filters=128, pool_size=3, pool_stride_size=2):
    my_input = Input(shape=(input_shape))
    for i in np.arange(num_res_blocks):
        if (i == 0):
            block_input = my_input
            x = BatchNormalization()(block_input)
        else:
            block_input = x
        for j in np.arange(cnn_per_res):
            x = Conv1D(num_filters, kernel_sizes[j], padding='same')(x)
            x = BatchNormalization()(x)
            if (j < cnn_per_res - 1):
                x = Activation('relu')(x)
        is_expand_channels = not (input_shape[0] == num_filters)
        if is_expand_channels:
            res_conn = Conv1D(num_filters, 1, padding='same')(block_input)
            res_conn = BatchNormalization()(res_conn)
        else:
            res_conn = BatchNormalization()(block_input)
        x = add([res_conn, x])
        x = Activation('relu')(x)
        if (i < 5):
            x = AveragePooling1D(pool_size=pool_size, strides=pool_stride_size)(x)
        num_filters = 2 * num_filters
        if max_filters < num_filters:
            num_filters = max_filters
    return my_input, x

# Function to create the main ResNet model for raw signals
def raw_signals_deep_ResNet(input_shape, num_channels):
    inputs = []
    l2_lambda = .001
    channel_outputs = []
    num_filters = 32
    for i in np.arange(num_channels):
        channel_resnet_input, channel_resnet_out = single_channel_resnet(input_shape, num_filters=num_filters, num_res_blocks=4, cnn_per_res=3, kernel_sizes=[8, 5, 5, 3], max_filters=64, pool_size=2, pool_stride_size=1)
        channel_outputs.append(channel_resnet_out)
        inputs.append(channel_resnet_input)
    spectral_outputs = []
    for x in inputs:
        spectro_x = mid_spectrogram_LSTM_layer(x)
        spectral_outputs.append(spectro_x)
    x = concatenate(channel_outputs, axis=-1)
    x = BatchNormalization()(x)
    x = GRU(65)(x)
    x = BatchNormalization()(x)
    s = concatenate(spectral_outputs, axis=-1)
    s = BatchNormalization()(s)
    x = concatenate([s, x])
    x = Dense(32, activation="relu", kernel_regularizer=l2(l2_lambda))(x)
    x = Dropout(0.25)(x)
    x = Dense(32, activation="relu", kernel_regularizer=l2(l2_lambda))(x)
    x = Dropout(0.25)(x)
    output = Dense(2, activation="relu")(x)
    model = Model(inputs=inputs, outputs=output)
    optimizer = optimizers.rmsprop(lr=.0001, decay=.0001)
    model.compile(optimizer=optimizer, loss='mse', metrics=['mae'])
    print(model.summary())
    return model

# Custom callback class for saving the best model weights based on validation accuracy
class custom_callback(tf.keras.callbacks.Callback):
    model_name = ""
    path = ""
    best = 100

    def __init__(self, dir, model_name, treshold=25):
        self.model_name = model_name
        self.path = dir + model_name + "/"
        self.best = treshold

    def on_train_begin(self, logs={}):
        self.losses = []
        self.acc = []
        self.val_losses = []
        self.val_acc = []
        return

    def on_train_end(self, logs={}):
        return

    def on_epoch_end(self, epoch, logs={}):
        self.acc.append(logs.get('mean_absolute_error'))
        self.val_acc.append(logs.get('val_mean_absolute_error'))
        if(logs.get('val_mean_absolute_error') < self.best):
            print("val_mean_absolute_error improved from " + str(self.best) + " to " + str(logs.get('val_mean_absolute_error')) + "...")
            self.best = logs.get('val_mean_absolute_error')
            self.model.save_weights("./Models/" + self.model_name + "_weights.h5")
        else:
            print("val_mean_absolute_error has not improved from " + str(self.best) + "...")

    def on_batch_begin(self, batch, logs={}):
        return

    def on_batch_end(self, batch, logs={}):
        return