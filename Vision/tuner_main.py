#!/bin/python

from argparse import ArgumentParser

import cv2

import simplejson

from blockmatcher import BlockMatcher
from calibrate_import import StereoCalibration
from utils import (find_files, BMTuner)

import pdb


def main():
    parser = ArgumentParser()
    parser.add_argument("calibration_folder")
    parser.add_argument("image_folder")
    parser.add_argument("--bm_settings", default="")
    args = parser.parse_args()

    settings = args.bm_settings

    if args.bm_settings:
        with open(settings) as settings_file:
            if settings_file:
                settings = args.bm_settings
            else:
                settings = None

    calibration = StereoCalibration(input_folder=args.calibration_folder)
    input_files = find_files(args.image_folder)
    block_matcher = BlockMatcher(settings=settings)
    image_pair = [cv2.imread(image) for image in input_files[:2]]

    gray1 = cv2.cvtColor(image_pair[0], cv2.COLOR_BGR2GRAY)
    gray2 = cv2.cvtColor(image_pair[1], cv2.COLOR_BGR2GRAY)

    gray_frames = [gray1, gray2]
        
    tuner = BMTuner(block_matcher, calibration, gray_frames)

    if args.bm_settings:
        block_matcher.save_settings(args.bm_settings)
        print "Settings saved."

if __name__ == "__main__":
    main()
