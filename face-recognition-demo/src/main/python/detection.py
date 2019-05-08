import cv2
from imutils.video import FPS
import base64
import io
from imageio import imread
import sys
import os

def face_detection(outputType):
    cwd = os.getcwd()
    if outputType == "file":
        fourcc = cv2.VideoWriter_fourcc(*"XVID")
        outputPath = cwd + '/src/main/savedVideos'
        if not os.path.exists(outputPath):
            os.makedirs(outputPath)
        out = cv2.VideoWriter(outputPath + '/sapphirized_detection_fps.avi', fourcc, 20.0, (320, 240))

    # Load a cascade file for detecting faces
    face_cascade = cv2.CascadeClassifier(cwd + '/src/main/resources/haarcascade_frontalface_default.xml')
    fps = FPS().start()

    while True:
        line = input()
        image = imread(io.BytesIO(base64.b64decode(line)))
        frame = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        (H, W) = frame.shape[:2]

        # Convert to grayscale
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        # Look for faces in the image using the loaded cascade file
        faces = face_cascade.detectMultiScale(gray, 1.1, 5)
        for (x, y, w, h) in faces:
            cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 255, 0), 2)

        # info to be displayed in the frame
        fps.update()
        fps.stop()
        info = [
            ("FPS", "{:.2f}".format(fps.fps()))
        ]

        # loop over the info tuples and draw them on our frame
        for (i, (k, v)) in enumerate(info):
            text = "{}: {}".format(k, v)
            cv2.putText(frame, text, (10, H - ((i * 20) + 20)), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)

        if outputType == "file":
            out.write(frame)
        elif outputType == "display":
            cv2.imshow('image', frame)
            cv2.waitKey(1)
        else:
            print("incorrect outputType specified", flush=True)
            break

        print("done", flush=True)

if __name__ == "__main__":
    outputType = sys.argv[1]
    face_detection(outputType)
