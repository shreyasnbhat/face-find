import face_recognition
import os
import time
import numpy as np

BASE = "./app/static/img/"


def get_encoding(path):
    image = face_recognition.load_image_file(path)
    try:
        encoding = face_recognition.face_encodings(image)[0]
        return encoding
    except:
        return False


def pre_process():
    try:
        known_encodings = np.load('encodings.npy')
        known_labels = np.load('labels.npy')
        print("File was found")
    # except FileNotFoundError:
    except (OSError, IOError):
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
    except (OSError, IOError):
        return
    face_distances = []
    face_distances = face_recognition.face_distance(known_encodings, b)
    print(face_distances)
    res_labels = []
    res_dist = []

    for i in range(len(face_distances)):
        if face_distances[i] < 0.6:
            res_labels.append(known_labels[i])
            res_dist.append(str(face_distances[i]))
    final_labels = [label for _, label in sorted(zip(res_dist, res_labels))]
    final_dist = sorted(res_dist)
    return final_labels, final_dist


if __name__ == '__main__':
    a, b = compute("henry2.jpg")
