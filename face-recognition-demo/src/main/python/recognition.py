import cv2
import numpy as np
import face_recognition
from pathlib import Path
import pickle
import os
import io
from imageio import imread

import base64


font = cv2.FONT_HERSHEY_DUPLEX


def load_weights(encoding_file):
    if Path(encoding_file).is_file():
        f = open(encoding_file, "rb")
        known_face_names = pickle.load(f)
        known_face_encodings = pickle.load(f)
        f.close()
        return os.stat(encoding_file).st_mtime, known_face_names, known_face_encodings
    else:
        print("No encoding file!")


def face_recognize():
    cwd = os.getcwd()
    while True:
        # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
        line = input()
        image = imread(io.BytesIO(base64.b64decode(line)))
        rgb_image = image[:, :, ::-1]

        # Call facial recognition
        encoding_file = cwd + "/src/main/resources/known_face_encodings.p"
        cached_stamp, known_face_names, known_face_encodings = load_weights(encoding_file)

        face_locations = face_recognition.face_locations(rgb_image)
        face_encodings = face_recognition.face_encodings(rgb_image, face_locations)

        bbox_list = []
        # Loop through each face found in the unknown image
        for (top, right, bottom, left), face_encoding in zip(face_locations, face_encodings):
            # See if the face is a match for the known face(s)
            matches = face_recognition.compare_faces(known_face_encodings, face_encoding, 0.5)

            name = "Unknown"

            if True in matches:
                if matches.count(True) > 1:
                    first_match_index = np.argmin(face_recognition.face_distance(known_face_encodings, face_encoding))
                else:
                    first_match_index = matches.index(True)
                name = known_face_names[first_match_index]

            bbox_list.append([(top, right, bottom, left), name])

        print(bbox_list)


face_recognize()
