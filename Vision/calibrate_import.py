import os
import cv2
import numpy as np

import pdb

class StereoCalibration(object):

    def _interact_with_folder(self, output_folder, action):
        if not action in ('r', 'w'):
            raise ValueError("action must be either 'r' or 'w'.")
        for key, item in self.__dict__.items():
            if isinstance(item, dict):
                for side in ("left", "right"):
                    filename = os.path.join(output_folder,
                                            "{}_{}.npy".format(key, side))
                    if action == 'w':
                        np.save(filename, self.__dict__[key][side])
                    else:
                        self.__dict__[key][side] = np.load(filename)
            else:
                filename = os.path.join(output_folder, "{}.npy".format(key))
                if action == 'w':
                    np.save(filename, self.__dict__[key])
                else:
                    self.__dict__[key] = np.load(filename)

    def __init__(self, calibration=None, input_folder=None):
        self.disp_to_depth_mat = None
        if input_folder:
            self.load(input_folder)

    def load(self, input_folder):
        self._interact_with_folder(input_folder, 'r')

    def export(self, output_folder):
        if not os.path.exists(output_folder):
            os.makedirs(output_folder)
        self._interact_with_folder(output_folder, 'w')