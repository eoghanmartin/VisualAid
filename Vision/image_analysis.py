import cv2
import pdb
import numpy as np

class ImageAnalysis(object):

    windows = "Image Analysis"
    BLACK = [0,0,0]
    BLUE = [255,0,0]

    def __init__(self, disparity):
        self.image_analyse = np.uint8(disparity)

        self.height = np.size(disparity, 0)
        self.width = np.size(disparity, 1)
        self.center_point = [(self.width/2), (self.height/2)]
        self.bottom_left = [0, self.height]
        self.bottom_right = [self.width, self.height]
        self.slope_right = float(self.center_point[1]-self.bottom_left[1])/(self.center_point[0]-self.bottom_left[0])
        self.slope_left = float(self.center_point[1]-self.bottom_right[1])/(self.center_point[0]-self.bottom_right[0])

        self.minVal = 0
        self.maxVal = 0
        self.minLoc = 0
        self.maxLoc = 0

        self.contours = None
        self.hierarchy = None

        self.image_analyse = self.morph_ops(self.image_analyse)
        self.min_max_vals(self.image_analyse)
        self.get_contours(self.image_analyse)

    def morph_ops(self, gray_image):
        kernel = np.ones((8,8),np.uint8)
        erosion = cv2.erode(gray_image,kernel,iterations = 1)
        dilation = cv2.dilate(erosion,kernel,iterations = 5)
        opening = cv2.morphologyEx(dilation, cv2.MORPH_OPEN, kernel)
        closing = cv2.morphologyEx(opening, cv2.MORPH_CLOSE, kernel)
        return closing

    def min_max_vals(self, image):
        gray = cv2.GaussianBlur(image, (51, 51), 0)
        (self.minVal, self.maxVal, self.minLoc, self.maxLoc) = cv2.minMaxLoc(gray)

    def get_contours(self, image):
        thresh = self.maxVal-40
        maxValue = 255
        th, dst = cv2.threshold(image, thresh, maxValue, cv2.THRESH_BINARY);
        img2, self.contours, self.hierarchy = cv2.findContours(dst, cv2.RETR_CCOMP , cv2.CHAIN_APPROX_NONE)

    def image_location(self):
        # Front right
        if self.maxLoc[0] > self.center_point[0]:
            if self.maxLoc[1] > self.center_point[1]:
                y_intercept = self.slope_right * self.maxLoc[0]
                if self.maxLoc[1] > y_intercept:
                    return "front"
        # Front left
        if self.maxLoc[0] < self.center_point[0]:
            if self.maxLoc[1] > self.center_point[1]:
                y_intercept = self.slope_left * self.maxLoc[0]
                if self.maxLoc[1] > y_intercept:
                    return "front"
        # Front center rectangle
        front_rect_top = 0 + self.height/3
        front_rect_left = self.width + self.width/3
        front_rect_right = self.width - self.width/3
        if self.maxLoc[0] < front_rect_right:
            if self.maxLoc[0] > front_rect_left:
                if self.maxLoc[1] > front_rect_top:
                    return "front"
        # Top right
        if self.maxLoc[0] > self.center_point[0]:
            if self.maxLoc[1] < self.center_point[1]:
                y_intercept = self.slope_left * self.maxLoc[0]
                if self.maxLoc[1] < y_intercept:
                    return "top"
        # Top left
        if self.maxLoc[0] < self.center_point[0]:
            if self.maxLoc[1] < self.center_point[1]:
                y_intercept = self.slope_right * self.maxLoc[0]
                if self.maxLoc[1] < y_intercept:
                    return "top"
        # Left
        if self.maxLoc[0] < self.center_point[0]:
            return "left"
        # Right
        if self.maxLoc[0] > self.center_point[0]:
            return "right"
        return "none"