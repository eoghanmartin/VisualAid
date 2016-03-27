import os
import base64
import httplib2

from apiclient.discovery import build
from oauth2client.client import GoogleCredentials

import logging
logging.basicConfig()

import cv2

import numpy as np

class VisionAPI(object):

	os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = os.path.join("", 'service.json')

	def __init__(self):
		API_DISCOVERY_FILE = 'https://vision.googleapis.com/$discovery/rest?version=v1'
		http = httplib2.Http()

		credentials = GoogleCredentials.get_application_default().create_scoped(['https://www.googleapis.com/auth/cloud-platform'])
		credentials.authorize(http)

		self.service = build('vision', 'v1', http=http, discoveryServiceUrl=API_DISCOVERY_FILE)

	def find_label(self, photo_file):
		with open(photo_file, 'rb') as image:
			image_content = base64.b64encode(image.read())
			service_request = self.service.images().annotate(
				body =
				{
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
			return label