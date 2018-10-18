import cv2
import base64
import imutils
import sys
import os

def extractFrames(sourceType):
    if sourceType == "video":
        pathIn = os.getcwd() + '/src/main/resources/sample_video.webm'
        cap = cv2.VideoCapture(pathIn)
    else:
        cap = cv2.VideoCapture(0)
    while True:
        # Capture frame-by-frame
        ret, frame = cap.read()
        if ret == True:
            frame = imutils.resize(frame, width=320)
            frame_serialize = base64.b64encode(cv2.imencode('.jpg', frame)[1].tobytes()).decode("utf-8")
            print(frame_serialize)
        else:
            print("could not read from source")
            break
    # When done, release the capture
    cap.release()

if __name__ == "__main__":
    source = sys.argv[1]
    extractFrames(source)