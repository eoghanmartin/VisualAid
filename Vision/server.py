from twisted.internet.protocol import Factory, Protocol
from twisted.internet import reactor
from time import sleep
import time
from pykeyboard import PyKeyboard
 
class connectionClass(Protocol):
    def connectionMade(self):
        self.factory.clients.append(self)
        print "New client: ", self.factory.clients

    def connectionLost(self, reason):
        self.factory.clients.remove(self)

    def dataReceived(self, data):
        print data
        pressing = ""
        if data[0]=="k" and len(data)>1:
            keys = data[1:]
            keys = keys.replace('\nk',"")
            for spacebar in keys:
                pressing+=spacebar
                kb.press_key(spacebar)
            sleep(0.01)
            for spacebar in keys:
                kb.release_key(spacebar)
        print "\nPressed: spacebar"

factory = Factory()
kb=PyKeyboard()
factory.protocol = connectionClass
factory.clients = []
reactor.listenTCP(8080, factory)
print "Server started"
reactor.run()