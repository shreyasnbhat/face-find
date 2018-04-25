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


def increment_count(user_id,child_status):
    db_session = DBSession()
    user_count = db_session.query(UserCount).filter_by(id=user_id).first()
    if user_count:
        count = 0
        if(child_status=="Missing"):
            count = user_count.missing_count
        else:
            count = user_count.found_count
        if count == 5:
            db_session.close()
            print("max reached")
            return 5, False
        else:
            count += 1
            if(child_status=="Missing"):
                setattr(user_count,'missing_count',count)
            else:
                setattr(user_count,'found_count',count)
            db_session.commit()
            db_session.close()
            print("temp_user_count")
            return count, True
    else:
        if child_status=="Missing":
            db_session.add(UserCount(id=user_id, missing_count=1, found_count=0))
        else:
            db_session.add(UserCount(id=user_id, missing_count=0, found_count=1))
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
@app.route('/api/images',methods=['POST'])
def get_images():
    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')

    if check_auth(user_id,password) is 'success':
        db_session = DBSession()
        images_missing = db_session.query(ImageDetailsMissing).filter_by(id=user_id).all()
        images_found = db_session.query(ImageDetailsFound).filter_by(id=user_id).all()

        response = ''
        response = response + str(len(images_missing)) + '|' + str(len(images_found)) + ','
        for i in images_missing:
            response = response + str(i.name) + '|' + str(i.gender) + '|' + str(i.age) + '|' + i.latitude+  '|' + i.longitude + '|' + i.phoneNo +','

        for i in images_found:
            response = response + str(i.name) + '|' + str(i.gender) + '|' + str(i.age) + '|' + i.latitude+  '|' + i.longitude + '|' + i.phoneNo +','
        db_session.close()
        print(response)
        return response
    else:
        return "Auth Failed"


@login_required
@app.route('/api/users/upload', methods=['POST'])
def upload():
    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')
    name = request.form['name']
    age = request.form['age']
    latitude = request.form['latitude']
    longitude = request.form['longitude']
    gender = request.form['gender']
    phoneNo = request.form['phone']
    child_status = request.form['child_status']


    db_session = DBSession()
    if check_auth(user_id, password) is 'success':
        count, flag = increment_count(user_id, child_status)
        if flag:
            image = base64.b64decode(request.form['image'])
            filename = user_id + '_'+ child_status[0] + str(count) + '.jpg'
            print("Filename is ", filename)
            path = UPLOAD_FOLDER + '/' + filename
            with open(path, 'wb') as f:
                f.write(image)

            print("Path is", path)
            encodings = get_encoding(path)
            encoding_str = json.dumps(list(encodings))
            if encodings is not False:
                if child_status=="Missing":
                    db_session.add(MissingImageEncoding(id=user_id,encoding=encoding_str,encoding_count=count))
                    db_session.add(ImageDetailsMissing(id = user_id, encoding_count=count, name=name, age=age, gender = gender, latitude=latitude, longitude =longitude, phoneNo=phoneNo))
                else:
                    db_session.add(FoundImageEncoding(id=user_id,encoding=encoding_str,encoding_count=count))
                    db_session.add(ImageDetailsFound(id = user_id, encoding_count = count, name=name, age=age, gender = gender, latitude=latitude, longitude =longitude, phoneNo=phoneNo))
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
    request_type = request.form['request-type']
    db_session = DBSession()

    if check_auth(user_id, password) is 'success':
        if request_type == 'Missing':
            known_users = [i[0] for i in db_session.query(MissingImageEncoding.id).filter(MissingImageEncoding.id != user_id).distinct()]
        elif request_type == 'Found':
            known_users = [i[0] for i in db_session.query(FoundImageEncoding.id).filter(FoundImageEncoding.id != user_id).distinct()]

        known_encodings = []
        known_labels = []
        print(known_users)

        for encoding_user in known_users:
            count = db_session.query(UserCount).filter_by(id=encoding_user).one()
            print( request_type == 'Missing' ,  request_type == 'Found')
            if request_type == 'Missing':
                for i in range(count.missing_count):
                    encoding_user_by_count = db_session.query(MissingImageEncoding).filter_by(id=encoding_user,encoding_count=i + 1).first()
                    print(encoding_user_by_count)
                    known_encodings.append(json.loads(encoding_user_by_count.encoding))
                    known_labels.append(encoding_user + '_M' + str(i + 1))
            elif request_type == 'Found':
                for i in range(count.found_count):
                    encoding_user_by_count = db_session.query(FoundImageEncoding).filter_by(id=encoding_user,encoding_count=i + 1).first()
                    print(encoding_user_by_count)
                    known_encodings.append(json.loads(encoding_user_by_count.encoding))
                    known_labels.append(encoding_user + '_F' + str(i + 1))

        current_user = db_session.query(UserCount).filter_by(id=user_id).first()
        result = set()

        if request_type == 'Missing':
            for i in range(current_user.found_count):
                b = get_encoding('./api/static/img/' + user_id + "_F" + str(i + 1) + ".jpg")
                face_distances = face_recognition.face_distance(np.array(known_encodings), b)
                for i in range(len(face_distances)):
                    if face_distances[i] < 0.6:
                        result.add((known_labels[i], str(face_distances[i])))
        elif request_type == 'Found':
            for i in range(current_user.missing_count):
                b = get_encoding('./api/static/img/' + user_id + "_M" + str(i + 1) + ".jpg")
                face_distances = face_recognition.face_distance(np.array(known_encodings), b)
                for i in range(len(face_distances)):
                    if face_distances[i] < 0.6:
                        result.add((known_labels[i], str(face_distances[i])))

        image_data_match = []
        for i in result:
            img_id_match = i[0].split('_')[0]

            img_enc_match = None
            img_details_match = None
            if request_type == 'Found':
                img_enc_match = i[0].split('F')[1]
                img_details_match = db_session.query(ImageDetailsFound).filter_by(id=img_id_match,encoding_count = img_enc_match).one()
            elif request_type == 'Missing':
                img_enc_match = i[0].split('M')[1]
                img_details_match = db_session.query(ImageDetailsMissing).filter_by(id=img_id_match,encoding_count = img_enc_match).one()

            if (request_type[0] == 'F' or request_type[0] == 'M'):
                image_data_match.append({'name':img_details_match.name,
                                         'gender':img_details_match.gender,
                                         'age':img_details_match.age,
                                         'latitude':img_details_match.latitude,
                                         'longitude':img_details_match.longitude,
                                         'phone':img_details_match.phoneNo })

        result = list(result)
        for i in range(len(result)):
            result[i]+=(image_data_match[i],)

        print(result)


        final_image_labels = set([generate_result(label,data) for label, _ ,data in sorted(result, key=itemgetter(1))])
        print(",".join(list(final_image_labels)))
        return ",".join(list(final_image_labels))

    else:
        return "Authentication Failed"


def generate_result(label,data):
    return label + '.jpg' + '|' + \
            data['name'] + '|' +\
            data['gender'] + '|' +\
            str(data['age']) + '|' +\
            data['latitude'] + '|' +\
            data['longitude'] + '|' +\
            data['phone']
