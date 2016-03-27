#!/bin/python

import argparse
import os

import cv2
import numpy as np

from blockmatcher import BlockMatcher
from calibration import StereoCalibration
from cameras import CalibratedPair

import pdb

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("calibration_folder")
    parser.add_argument("left_image")
    parser.add_argument("right_image")
    parser.add_argument("output_folder")
    parser.add_argument("--bm_settings")
    args = parser.parse_args()

    image_pair = [cv2.imread(image) for image in [args.left_image, args.right_image]]
    calib_folder = args.calibration_folder
    block_matcher = BlockMatcher()
    if args.bm_settings:
        block_matcher.load_settings(args.bm_settings)

    camera_pair = CalibratedPair(None, StereoCalibration(input_folder=calib_folder), block_matcher)
    disparity_image = camera_pair.get_disparity(image_pair)

    imCopy = disparity_image.copy()

    BLACK = [0,0,0]
    BLUE = [255,0,0]

    img = np.uint8(imCopy)

    kernel = np.ones((8,8),np.uint8)
    erosion = cv2.erode(img,kernel,iterations = 1)
    dilation = cv2.dilate(erosion,kernel,iterations = 5)
    opening = cv2.morphologyEx(dilation, cv2.MORPH_OPEN, kernel)
    closing = cv2.morphologyEx(opening, cv2.MORPH_CLOSE, kernel)

    gray = cv2.GaussianBlur(closing, (51, 51), 0)
    (minVal, maxVal, minLoc, maxLoc) = cv2.minMaxLoc(gray)

    thresh = maxVal-40
    maxValue = 255
    th, dst = cv2.threshold(closing, thresh, maxValue, cv2.THRESH_BINARY);

    im2, contours, hierarchy = cv2.findContours(dst, cv2.RETR_CCOMP , cv2.CHAIN_APPROX_NONE)
    
    cv2.circle(dst, maxLoc, 51, (255, 0, 0), 2)
    cv2.drawContours(dst, contours, -1, BLUE, 2)
    cv2.imshow("Contours", dst)

    if cv2.waitKey(0) & 0xFF == ord('q'):
        filename = "disparity.jpg"
        output_path = os.path.join(args.output_folder, filename)
        cv2.imwrite(output_path, disparity_image)

if __name__ == "__main__":
    main()