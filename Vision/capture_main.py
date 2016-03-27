#!/bin/python

from argparse import ArgumentParser
import os

import cv2

from progressbar import ProgressBar, Bar, Percentage
from cameras import StereoPair
from utils import find_files

import pdb

def main():
    parser = ArgumentParser()
    parser.add_argument("left", type=int)
    parser.add_argument("right", type=int)
    parser.add_argument("num_pictures", type=int)
    parser.add_argument("output_folder")
    args = parser.parse_args()

    progress = ProgressBar(maxval=args.num_pictures,
                          widgets=[Bar("=", "[", "]"),
                          " ", Percentage()])

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)
    progress.start()

    with StereoPair([args.left, args.right]) as pair:
        for i in range(args.num_pictures):
            frames = pair.get_frames()
            for side, frame in zip(("left", "right"), frames):
                number_string = str(i + 1).zfill(len(str(args.num_pictures)))
                filename = "{}_{}.jpg".format(side, number_string)
                output_path = os.path.join(args.output_folder, filename)
                cv2.imwrite(output_path, frame)
            progress.update(progress.maxval - (args.num_pictures - i))
            for i in range(10):
                pair.show_frames(1)
        progress.finish()


if __name__ == "__main__":
    main()