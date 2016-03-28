#!/bin/python

from argparse import ArgumentParser
import os

import cv2

import numpy as np

from twisted.internet.protocol import Factory, Protocol
from twisted.internet import reactor
from time import sleep
import time
from pykeyboard import PyKeyboard

from cameras import (StereoPair, CalibratedPair)
from calibrate_import import StereoCalibration
from blockmatcher import BlockMatcher
from point_cloud import PointCloud
from image_analysis import ImageAnalysis
from vision_api import VisionAPI

import pdb

class ConnectionClass(Protocol):

    def connectionMade(self):
        self.factory.clients.append(self)
        print "New client: ", self.factory.clients
        self.transport.write("Connected\r\n")

    def connectionLost(self, reason):
        self.factory.clients.remove(self)

    def dataReceived(self, data):
        if "capture" in data:
            print "Running visual assist program..."
            location = self.run_main()
            self.transport.write(location + "\r\n")
            return
        else:
            print data
            return

    def run_main(self):
        ply_header = '''ply
            format ascii 1.0
            element vertex %(vert_num)d
            property float x
            property float y
            property float z
            property uchar red
            property uchar green
            property uchar blue
            end_header
            '''

        def write_ply(fn, verts, colors):
            verts = verts.reshape(-1, 3)
            colors = colors.reshape(-1, 3)
            verts = np.hstack([verts, colors])
            with open(fn, 'wb') as f:
                f.write((ply_header % dict(vert_num=len(verts))).encode('utf-8'))
                np.savetxt(f, verts, fmt='%f %f %f %d %d %d ')

        block_matcher = BlockMatcher()
        if args.bm_settings:
            block_matcher.load_settings(args.bm_settings)

        if not os.path.exists(args.disparity_folder):
            os.makedirs(args.daisparity_folder)

        pair = StereoPair([args.left, args.right])

        frames = pair.get_frames()
        for side, frame in zip(("left", "right"), frames):
            filename = "image_{}.jpg".format(side)
            output_path = os.path.join(args.output_folder, filename)
            cv2.imwrite(output_path, frame)

        for i in range(10):
            pair.show_frames(1)

        disparity_image = block_matcher.get_disparity(frames)

        print "Post filter disparity map for locations..."

        imgCopy = disparity_image.copy()

        analysis = ImageAnalysis(imgCopy)
        
        cv2.circle(analysis.image_analyse, analysis.maxLoc, 51, analysis.BLUE, 2)
        cv2.drawContours(analysis.image_analyse, analysis.contours, -1, analysis.BLUE, 2)

        f_height = np.size(analysis.image_analyse, 0)
        f_width = np.size(analysis.image_analyse, 1)
        dim = (f_width/2, f_height/2)        
        resized = cv2.resize(analysis.image_analyse, dim)
        cv2.imshow("Contours", resized)

        location = analysis.image_location()

        print "Closest object is: " + location

        print "Generating 3d point cloud..."

        camera_pair = CalibratedPair(None,
                                StereoCalibration(input_folder=args.calibration_folder),
                                block_matcher)
        points = camera_pair.get_point_cloud(frames)
        points = points.filter_infinity()
        points.write_ply(args.ply_file)

        print "PLY file written."

        image_path = os.path.join(args.output_folder, "image_left.jpg")
        api = VisionAPI()
        label = api.find_label(image_path)

        print('Found label: %s for %s' % (label, "image_left"))

        return location

if __name__ == "__main__":

    parser = ArgumentParser()
    parser.add_argument("left", type=int)
    parser.add_argument("right", type=int)
    parser.add_argument("disparity_folder")
    parser.add_argument("calibration_folder")
    parser.add_argument("output_folder")
    parser.add_argument("bm_settings")
    parser.add_argument("ply_file")
    args = parser.parse_args()

    left = args.left
    right = args.right
    disparity_folder = args.disparity_folder
    calibration_folder = args.calibration_folder
    output_folder = args.output_folder
    bm_settings = args.bm_settings
    ply_file = args.ply_file

    kb=PyKeyboard()

    factory = Factory()
    factory.protocol = ConnectionClass
    factory.clients = []
    reactor.listenTCP(8080, factory)
    print "Server started"
    reactor.run()