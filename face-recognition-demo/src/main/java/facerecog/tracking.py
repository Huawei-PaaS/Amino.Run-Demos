import cv2
import os
import sys
import io
from imageio import imread
import ast

import base64

from imutils.video import FPS


font = cv2.FONT_HERSHEY_DUPLEX


def face_tracking(outputType):
    cwd = os.getcwd()
    if outputType == "file":
        fourcc = cv2.VideoWriter_fourcc(*"XVID")
        outputPath = cwd + '/src/main/savedVideos'
        if not os.path.exists(outputPath):
            os.makedirs(outputPath)
        out = cv2.VideoWriter(outputPath + '/sapphirized_tracking_fps.avi', fourcc, 20.0, (320, 240))

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
        found_face = False

        if len(faces) > 0:
            found_face = True
            # signal the java layer to identify faces
            print("identify", flush=True)
            # get the box list from the java layer
            bbox_list_str = input()
            bbox_list = ast.literal_eval(bbox_list_str)
            if len(bbox_list) > 0:
                tracker = cv2.MultiTracker_create()
                for box in bbox_list:
                    top, right, bottom, left = box[0]
                    name = box[1]
                    track = cv2.TrackerTLD_create()  # TrackerMIL_create()#TrackerKCF_create()
                    ok = tracker.add(track, frame, (left, top, right - left, bottom - top))
                new_boxes = None
                refresh = 0
                while new_boxes is None or len(new_boxes) > 0:
                    line = input()
                    print("next", flush=True)

                    image = imread(io.BytesIO(base64.b64decode(line)))
                    frame = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

                    ok, new_boxes = tracker.update(frame)
                    for i in range(len(new_boxes)):
                        p1 = (int(new_boxes[i][0]), int(new_boxes[i][1]))
                        p2 = (int(new_boxes[i][0] + new_boxes[i][2]), int(new_boxes[i][1] + new_boxes[i][3]))
                        p3 = (int(new_boxes[i][0]), int(new_boxes[i][1] + new_boxes[i][3]) - 10)
                        cv2.rectangle(frame, p1, p2, (0, 0, 255), 1)
                        cv2.rectangle(frame, p3, p2, (0, 0, 255), cv2.FILLED)
                        cv2.putText(frame, bbox_list[i][1],
                                    (int(new_boxes[i][0]), int(new_boxes[i][1] + new_boxes[i][3]) - 4), font,
                                    0.3, (0, 0, 0), 1)
                    refresh += 1
                    if refresh == 16:
                        # out.write(frame)
                        break

                    # info to be displayed in the frame
                    fps.update()
                    fps.stop()
                    info = [
                        ("FPS", "{:.2f}".format(fps.fps()))
                    ]

                    # loop over the info tuples and draw them on our frame
                    (H, W) = frame.shape[:2]
                    for (i, (k, v)) in enumerate(info):
                        text = "{}: {}".format(k, v)
                        cv2.putText(frame, text, (10, H - ((i * 20) + 20)), cv2.FONT_HERSHEY_SIMPLEX, 0.6,
                                    (0, 0, 255), 2)

                    if outputType == "file":
                        out.write(frame)
                    elif outputType == "display":
                        cv2.imshow('image', frame)
                        cv2.waitKey(1)
                    else:
                        print("incorrect outputType specified", flush=True)
                        break

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

        if found_face == False:
            print("done", flush=True)


if __name__ == "__main__":
    outputType = sys.argv[1]
    face_tracking(outputType)
