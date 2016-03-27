#!/bin/python

import argparse
import os

import cv2
import numpy as np

from blockmatcher import BlockMatcher
from calibration import StereoCalibration
from cameras import CalibratedPair
from image_analysis import ImageAnalysis

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

    imgCopy = disparity_image.copy()

    analysis = ImageAnalysis(imgCopy)
    
    cv2.circle(analysis.image_analyse, analysis.maxLoc, 51, analysis.BLUE, 2)
    cv2.drawContours(analysis.image_analyse, analysis.contours, -1, analysis.BLUE, 2)

    f_height = np.size(analysis.image_analyse, 0)
    f_width = np.size(analysis.image_analyse, 1)
    dim = (f_width/2, f_height/2)
    resized = cv2.resize(analysis.image_analyse, dim)
    cv2.imshow("Contours", resized)

    if cv2.waitKey(0) & 0xFF == ord('q'):
        filename = "disparity.jpg"
        output_path = os.path.join(args.output_folder, filename)
        cv2.imwrite(output_path, disparity_image)

if __name__ == "__main__":
    main()