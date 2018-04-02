from flask import render_template, request, redirect, url_for, abort, flash, session, g, send_from_directory, send_file
from flask.globals import session as session_obj
from flask_login import login_user, login_required, logout_user, current_user
from sqlalchemy.orm import exc
import json, os, time, bcrypt, hashlib
from api import *
import random
import json
import numpy as np
from werkzeug.utils import secure_filename
import base64
from operator import itemgetter
from main import get_encoding, face_recognition
import sys

def check_auth(user_id, password):
    db_session = DBSession()
    user = db_session.query(User).filter_by(id=user_id).first()
    user_credentials = db_session.query(AuthStore).filter_by(id=user_id).first()
    db_session.close()
    if user:
        print(type(password), type(user_credentials.phash))
        if bcrypt.checkpw(password, user_credentials.phash):
            login_user(user_credentials)
            return "success"
        else:
            return "Wrong Password"
    else:
        return "No such user exists"


def increment_count_missing(user_id):
    db_session = DBSession()
    user_count = db_session.query(UserCountMissing).filter_by(id=user_id).first()
    if user_count:
        if user_count.count == 5:
            db_session.close()
            print("max reached")
            return 5, False
        else:
            user_count.count += 1
            temp_user_count = user_count.count
            db_session.commit()
            db_session.close()
            print("temp_user_count")
            return temp_user_count, True
    else:
        db_session.add(UserCountMissing(id=user_id, count=1))
        db_session.commit()
        db_session.close()
        return 1, True
def increment_count_found(user_id):
    db_session = DBSession()
    user_count = db_session.query(UserCountFound).filter_by(id=user_id).first()
    if user_count:
        if user_count.count == 5:
            db_session.close()
            print("max reached")
            return 5, False
        else:
            user_count.count += 1
            temp_user_count = user_count.count
            db_session.commit()
            db_session.close()
            print("temp_user_count")
            return temp_user_count, True
    else:
        db_session.add(UserCountFound(id=user_id, count=1))
        db_session.commit()
        db_session.close()
        return 1, True


@app.route('/api/users/add', methods=['POST'])
def add_user():
    db_session = DBSession()

    name = request.form['username']
    password = request.form['password'].encode('utf-8')
    user_id = request.form['user-id']
    age = request.form['age']
    gender = request.form['gender']

    if db_session.query(User).filter_by(id=user_id).first():
        return "Duplicate User ID"
    else:
        db_session.add(User(id=user_id,
                            name=name,
                            gender=gender,
                            age=age))

        salt = bcrypt.gensalt()
        phash = bcrypt.hashpw(password, salt)
        db_session.add(AuthStore(id=user_id,
                                 phash=phash,
                                 salt=salt,
                                 isAdmin=True))
        db_session.commit()
        db_session.close()
        return "User Created Successfully"


@app.route('/api/users/delete', methods=['POST'])
def remove_user():
    db_session = DBSession()

    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')

    user = db_session.query(User).filter_by(id=user_id).first()
    user_credentials = db_session.query(AuthStore).filter_by(id=user_id).first()

    if user:
        if bcrypt.checkpw(password, user_credentials.phash):
            db_session.delete(user_credentials)
            db_session.delete(user)
            db_session.commit()
            db_session.close()
            return "success"
    else:
        db_session.close()
        return "user doesn't exist"


@app.route('/api/authenticate', methods=['POST'])
def authenticate():
    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')
    return check_auth(user_id, password)


@login_required
@app.route('/api/test', methods=['POST'])
def test():
    db_session = DBSession()

    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')

    print(type(password))

    if check_auth(user_id, password) is 'success':
        user = db_session.query(User).filter_by(id=user_id).first()
        return str(user.age)
    else:
        return "not authenticated"


@login_required
@app.route('/api/users/upload/missing', methods=['POST'])
def upload_missing():
    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')
    name = request.form['name']
    age = request.form['age']
    location = request.form['location']
    gender = request.form['gender']


    db_session = DBSession()
    if check_auth(user_id, password) is 'success':
        count, flag = increment_count_missing(user_id)
        if flag:
            image = base64.b64decode(request.form['image'])
            filename = user_id + '_m' + str(count) + '.jpg'
            print("Filename is ", filename)
            path = UPLOAD_FOLDER + '/' + filename
            with open(path, 'wb') as f:
                f.write(image)

            print("Path is", path)
            encodings = get_encoding(path)
            encoding_str = json.dumps(list(encodings))
            if encodings is not False:
                db_session.add(MissingImageEncoding(id=user_id,encoding=encoding_str,encoding_count=count))
                db_session.add(ImageDetailsMissing(id = user_id, encoding_count=count, name=name, age=age, gender = gender, location=location))

                db_session.commit()
                db_session.close()
                return "Upload Successful"
            else:
                return "No face found in Image"
        else:
            return "Max Images Sent"

    return "Upload Failed"

@login_required
@app.route('/api/users/upload/found', methods=['POST'])
def upload_found():
    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')
    name = request.form['name']
    age = request.form['age']
    location = request.form['location']
    gender = request.form['gender']

    db_session = DBSession()
    if check_auth(user_id, password) is 'success':
        count, flag = increment_count_found(user_id)
        if flag:
            image = base64.b64decode(request.form['image'])
            filename = user_id + '_f' + str(count) + '.jpg'
            print("Filename is ", filename)
            path = UPLOAD_FOLDER + '/' + filename
            with open(path, 'wb') as f:
                f.write(image)

            print("Path is", path)
            encodings = get_encoding(path)
            encoding_str = json.dumps(list(encodings))
            if encodings is not False:
                db_session.add(FoundImageEncoding(id=user_id,encoding=encoding_str,encoding_count=count))
                db_session.add(ImageDetailsFound(id = user_id, encoding_count = count, name=name, age=age, gender = gender, location=location))
                db_session.commit()
                db_session.close()
                return "Upload Successful"
            else:
                return "No face found in Image"
        else:
            return "Max Images Sent"

    return "Upload Failed"


@login_required
@app.route('/api/match', methods=['POST'])
def find_matches():
    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')
    db_session = DBSession()

    print(user_id, password)

    if check_auth(user_id, password) is 'success':
        known_users = [i[0] for i in db_session.query(MissingImageEncoding.id).filter(MissingImageEncoding.id != user_id).distinct()]
        print(known_users)
        known_encodings = []
        known_labels = []
        for encoding_user in known_users:
            count = db_session.query(UserCountMissing).filter_by(id=encoding_user).one()
            print(count.count)
            for i in range(count.count):
                encoding_user_by_count = db_session.query(MissingImageEncoding).filter_by(id=encoding_user,encoding_count=i + 1).first()
                print(encoding_user_by_count)
                known_encodings.append(json.loads(encoding_user_by_count.encoding))
                known_labels.append(encoding_user + '_m' + str(i + 1))

        current_user = db_session.query(UserCountFound).filter_by(id=user_id).first()
        result = set()

        for i in range(current_user.count):
            b = get_encoding('./api/static/img/' + user_id + "_f" + str(i + 1) + ".jpg")
            face_distances = face_recognition.face_distance(np.array(known_encodings), b)
            for i in range(len(face_distances)):
                if face_distances[i] < 0.6:
                    result.add((known_labels[i], str(face_distances[i])))

        final_image_labels = set([label + '.jpg' for label, _ in sorted(result, key=itemgetter(1))])
        print(",".join(list(final_image_labels)))
        return ",".join(list(final_image_labels))

    else:
        return "Authentication Failed"
