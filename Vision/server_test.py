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
            location = "left:20,20\r\n"
            self.transport.write(location)
        else:
            print data

if __name__ == "__main__":

    kb=PyKeyboard()

    factory = Factory()
    factory.protocol = ConnectionClass
    factory.clients = []
    reactor.listenTCP(8080, factory)
    print "Server started"
    reactor.run()