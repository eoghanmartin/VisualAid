from argparse import ArgumentParser
from functools import partial
import os

import cv2
import numpy as np
from calibration import StereoCalibrator

import pdb

def find_files(folder):
    files = [i for i in os.listdir(folder) if i.startswith("left")]
    files.sort()
    for i in range(len(files)):
        insert_string = "right{}".format(files[i * 2][4:])
        files.insert(i * 2 + 1, insert_string)
    files = [os.path.join(folder, filename) for filename in files]
    return files

class BMTuner(object):

    window_name = "BM Tuner"

    def _set_value(self, parameter, new_value):
        try:
            self.block_matcher.__setattr__(parameter, new_value)
        except Exception:
            return
        self.update_disparity_map()

    def _initialize_trackbars(self):
        for parameter in self.block_matcher.parameter_maxima.keys():
            maximum = self.block_matcher.parameter_maxima[parameter]
            if not maximum:
                maximum = self.shortest_dimension
            cv2.createTrackbar(parameter, self.window_name,
                               self.block_matcher.__getattribute__(parameter),
                               maximum,
                               partial(self._set_value, parameter))

    def _save_bm_state(self):
        for parameter in self.block_matcher.parameter_maxima.keys():
            self.bm_settings[parameter].append(
                               self.block_matcher.__getattribute__(parameter))

    def __init__(self, block_matcher, calibration, image_pair):
        self.calibration = calibration
        self.pair = image_pair
        self.block_matcher = block_matcher
        self.shortest_dimension = min(self.pair[0].shape[:2])
        self.bm_settings = {}
        for parameter in self.block_matcher.parameter_maxima.keys():
            self.bm_settings[parameter] = []
        cv2.namedWindow(self.window_name)
        self._initialize_trackbars()
        self.tune_pair(image_pair)

    def update_disparity_map(self):
        disparity = self.block_matcher.get_disparity(self.pair)
        norm_coeff = 255 / disparity.max()
        disp_image = disparity * norm_coeff / 255

        f_height = np.size(disp_image, 0)
        f_width = np.size(disp_image, 1)
        dim = (f_width/2, f_height/2)
        resized = cv2.resize(disp_image, dim)
        cv2.imshow(self.window_name, resized)
        
        if cv2.waitKey(0) & 0xFF == ord('q'):
            cv2.destroyWindow(self.window_name)

    def tune_pair(self, pair):
        self._save_bm_state()
        self.pair = pair
        self.update_disparity_map()
