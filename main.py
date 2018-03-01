import face_recognition
import os
import time
import numpy as np

BASE = "./app/static/img/"


def get_encoding(path):
    image = face_recognition.load_image_file(path)
    encoding = face_recognition.face_encodings(image)[0]
    return encoding


def pre_process():
    try:
        known_encodings = np.load('encodings.npy')
        known_labels = np.load('labels.npy')
        print("File was found")
    except FileNotFoundError:
        print("File not found")
        errors = 0
        known_encodings = []
        known_labels = []
        init = time.time()
        for image in os.listdir(BASE + "known"):
            print(image)
            try:
                known_encodings.append(get_encoding(BASE + "known/" + image))
                known_labels.append(image[:-4])
            except:
                print("Error with " + image)
                errors += 1
        fin = time.time()
        print("Took", fin - init, "seconds to process encodings!")
        print("Average time per encoding computation", (fin - init) / (len(os.listdir(BASE + "known")) - errors))
        np.save('encodings.npy', np.array(known_encodings))
        np.save('labels.npy', np.array(known_labels))

    return known_encodings, known_labels


def compute(filepath):
    known_encodings, known_labels = pre_process()
    try:
        b = get_encoding(BASE + "unknown/" + filepath)
    except FileNotFoundError:
        return

    results = face_recognition.compare_faces(known_encodings, b,tolerance=0.6)
    res_labels = []
    for i in range(len(results)):
        if results[i] == True:
            res_labels.append(known_labels[i])

    return res_labels

if __name__ == '__main__':
    a = compute(BASE + "known/2015A7PS033G.jpg")

