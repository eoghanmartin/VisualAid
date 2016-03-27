import cv2
import pdb
import numpy as np

class ImageAnalysis(object):

    windows = "Image Analysis"
    BLACK = [0,0,0]
    BLUE = [255,0,0]

    def __init__(self, disparity):
        self.image_analyse = np.uint8(disparity)

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