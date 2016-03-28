#!/bin/python
import argparse

import cv2

from blockmatcher import BlockMatcher
from calibrate_import import StereoCalibration
from cameras import CalibratedPair

import pdb


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("calibration")
    parser.add_argument("left")
    parser.add_argument("right")
    parser.add_argument("output")
    parser.add_argument("--bm_settings")
    args = parser.parse_args()

    image_pair = [cv2.imread(image) for image in [args.left, args.right]]
    calib_folder = args.calibration
    block_matcher = BlockMatcher()
    if args.bm_settings:
        block_matcher.load_settings(args.bm_settings)

    camera_pair = CalibratedPair(None,
                                StereoCalibration(input_folder=calib_folder),
                                block_matcher)
    points = camera_pair.get_point_cloud(image_pair)
    points = points.filter_infinity()
    points.write_ply(args.output)

if __name__ == "__main__":
    main()
