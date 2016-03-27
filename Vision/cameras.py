import cv2
import pdb
import numpy as np

from point_cloud import PointCloud

class StereoPair(object):

    windows = ["{} camera".format(side) for side in ("Left", "Right")]

    def __init__(self, devices):
        self.captures = [cv2.VideoCapture(device) for device in devices]

    def __enter__(self):
        return self

    def __exit__(self, type, value, traceback):
        for capture in self.captures:
            capture.release()
        for window in self.windows:
            cv2.destroyWindow(window)

    def get_frames(self):
        return [capture.read()[1] for capture in self.captures]

    def show_frames(self, wait=0):
        for window, frame in zip(self.windows, self.get_frames()):
            f_height = np.size(frame, 0)
            f_width = np.size(frame, 1)
            dim = (f_width/2, f_height/2)
            resized = cv2.resize(frame, dim)
            cv2.imshow(window, resized)
        cv2.waitKey(wait)

    def show_videos(self):
        while True:
            self.show_frames(1)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

class CalibratedPair(StereoPair):
    
    def __init__(self, devices, calibration, block_matcher):
        if devices:
            super(CalibratedPair, self).__init__(devices)
        self.calibration = calibration
        self.block_matcher = block_matcher

    def get_frames(self):
        frames = super(CalibratedPair, self).get_frames()
        return self.calibration.rectify(frames)

    def get_disparity(self, pair):
        disparity = self.block_matcher.get_disparity(pair)
        return disparity

    def get_point_cloud(self, pair):
        disparity = self.block_matcher.get_disparity(pair)
        points = self.block_matcher.get_3d(disparity, self.calibration.disp_to_depth_mat)
        colors = cv2.cvtColor(pair[0], cv2.COLOR_BGR2RGB)
        return PointCloud(points, colors)
