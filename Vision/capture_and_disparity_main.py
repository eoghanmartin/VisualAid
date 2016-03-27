#!/bin/python

from argparse import ArgumentParser
import os
import base64
import httplib2

from apiclient.discovery import build
from oauth2client.client import GoogleCredentials

import logging
logging.basicConfig()

import cv2

import numpy as np

from cameras import (StereoPair, CalibratedPair)
from calibration import StereoCalibration
from blockmatcher import BlockMatcher
from point_cloud import PointCloud
from image_analysis import ImageAnalysis

import pdb

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = os.path.join("", 'service.json')

def main():
    parser = ArgumentParser()
    parser.add_argument("left", type=int)
    parser.add_argument("right", type=int)
    parser.add_argument("daisparity_folder")
    parser.add_argument("calibration_folder")
    parser.add_argument("output_folder")
    parser.add_argument("bm_settings")
    parser.add_argument("ply_file")
    args = parser.parse_args()

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

    def find_label(photo_file):

        API_DISCOVERY_FILE = 'https://vision.googleapis.com/$discovery/rest?version=v1'
        http = httplib2.Http()

        credentials = GoogleCredentials.get_application_default().create_scoped(
          ['https://www.googleapis.com/auth/cloud-platform'])
        credentials.authorize(http)

        service = build('vision', 'v1', http=http, discoveryServiceUrl=API_DISCOVERY_FILE)

        with open(photo_file, 'rb') as image:
            image_content = base64.b64encode(image.read())
            service_request = service.images().annotate(
              body={
                'requests': [{
                  'image': {
                    'content': image_content
                   },
                  'features': [{
                    'type': 'LABEL_DETECTION',
                    'maxResults': 1,
                   }]
                 }]
              })
            response = service_request.execute()
            label = response['responses'][0]['labelAnnotations'][0]['description']
            print('Found label: %s for %s' % (label, photo_file))
            return 0

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

    if not os.path.exists(args.daisparity_folder):
        os.makedirs(args.daisparity_folder)

    pair = StereoPair([args.left, args.right])

    while(True):
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

        print "Generating 3d point cloud..."

        camera_pair = CalibratedPair(None,
                                StereoCalibration(input_folder=args.calibration_folder),
                                block_matcher)
        points = camera_pair.get_point_cloud(frames)
        points = points.filter_infinity()
        points.write_ply(args.ply_file)

        print "PLY file written."

        image_path = os.path.join(args.output_folder, "image_left.jpg")
        find_label(image_path)

        if cv2.waitKey(0) & 0xFF == ord('q'):
            continue

if __name__ == "__main__":
    main()