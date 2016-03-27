#!/bin/python

import argparse
import os
import time

import cv2

from cameras import StereoPair


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("devices", type=int, nargs=2)
    parser.add_argument("--output_folder")
    parser.add_argument("--interval", type=float, default=1)
    args = parser.parse_args()

    with StereoPair(args.devices) as pair:
            pair.show_videos()

if __name__ == "__main__":
    main()
