import cv2

import simplejson
import numpy as np

import pdb

class BlockMatcher(object):

    parameter_maxima = {"minDisparity": None,
                       "numDisparities": None,
                       "blockSize": 11,
                       "P1": None,
                       "P2": None,
                       "disp12MaxDiff": None,
                       "uniquenessRatio": 15,
                       "speckleWindowSize": 200,
                       "speckleRange": 2,
                       "mode": 1}

    def __init__(self, min_disparity=16, num_disp=96, block_size=3,
                 uniqueness=10, speckle_window_size=100, speckle_range=32,
                 p1=216, p2=864, max_disparity=1, mode=False,
                 settings=None):
        self._min_disparity = min_disparity
        self._num_disp = num_disp
        self._block_size = block_size
        self._uniqueness = uniqueness
        self._speckle_window_size = speckle_window_size
        self._speckle_range = speckle_range
        self._P1 = p1
        self._P2 = p2
        self._max_disparity = max_disparity
        self._mode = mode
        self._replace_bm()
        if settings:
            self.load_settings(settings)

    def _replace_bm(self):
        self._block_matcher = cv2.StereoSGBM_create(minDisparity=self._min_disparity,
                        numDisparities=self._num_disp,
                        blockSize=self._block_size,
                        uniquenessRatio=self._uniqueness,
                        speckleWindowSize=self._speckle_window_size,
                        speckleRange=self._speckle_range,
                        disp12MaxDiff=self._max_disparity,
                        P1=self._P1,
                        P2=self._P2,
                        mode = self._mode)

    def load_settings(self, settings):
        with open(settings) as settings_file:
            settings_dict = simplejson.load(settings_file)
        for key, value in settings_dict.items():
            self.__setattr__(key, value)

    def save_settings(self, settings_file):
        settings = {}
        for parameter in self.parameter_maxima:
            settings[parameter] = self.__getattribute__(parameter)
        with open(settings_file, "w") as settings_file:
            simplejson.dump(settings, settings_file)

    def get_disparity(self, pair):
        disp = self._block_matcher.compute(pair[0],
                                          pair[1]).astype(np.float32) / 16.0
        disp_image = (disp-self._min_disparity)/self._num_disp
        f_height = np.size(disp_image, 0)
        f_width = np.size(disp_image, 1)
        dim = (f_width/2, f_height/2)
        resized = cv2.resize(disp_image, dim)
        #cv2.imshow('disparity', resized)
        return disp

    @classmethod
    def get_3d(cls, disparity, disparity_to_depth_map):
        return cv2.reprojectImageTo3D(disparity, disparity_to_depth_map)

    @property
    def minDisparity(self):
        return self._min_disparity

    @minDisparity.setter
    def minDisparity(self, value):
        self._min_disparity = value
        self._replace_bm()

    @property
    def numDisparities(self):
        return self._num_disp

    @numDisparities.setter
    def numDisparities(self, value):
        if value > 0 and value % 16 == 0:
            self._num_disp = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def blockSize(self):
        return self._block_size

    @blockSize.setter
    def blockSize(self, value):
        if value >= 1 and value <= 11 and value % 2:
            self._block_size = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def uniquenessRatio(self):
        return self._uniqueness

    @uniquenessRatio.setter
    def uniquenessRatio(self, value):
        if value >= 5 and value <= 15:
            self._uniqueness = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def speckleWindowSize(self):
        return self._speckle_window_size

    @speckleWindowSize.setter
    def speckleWindowSize(self, value):
        if value >= 0 and value <= 200:
            self._speckle_window_size = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def speckleRange(self):
        return self._speckle_range

    @speckleRange.setter
    def speckleRange(self, value):
        if value >= 0:
            self._speckle_range = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def disp12MaxDiff(self):
        return self._max_disparity

    @disp12MaxDiff.setter
    def disp12MaxDiff(self, value):
        self._max_disparity = value
        self._replace_bm()

    @property
    def P1(self):
        return self._P1

    @P1.setter
    def P1(self, value):
        if value < self.P2:
            self._P1 = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def P2(self):
        return self._P2

    @P2.setter
    def P2(self, value):
        if value > self.P1:
            self._P2 = value
        else:
            raise Exception
        self._replace_bm()

    @property
    def mode(self):
        return self._mode

    @mode.setter
    def mode(self, value):
        self._mode = bool(value)
        self._replace_bm()
