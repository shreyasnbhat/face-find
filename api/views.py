from flask import render_template, request, redirect, url_for, abort, flash, session, g, send_from_directory
from flask.globals import session as session_obj
from flask_login import login_user, login_required, logout_user, current_user
from sqlalchemy.orm import exc
import json, os, time, bcrypt, hashlib
from api import *
import random
import json
from werkzeug.utils import secure_filename


@app.route('/api/users/add', methods=['POST'])
def add_user():
    db_session = DBSession()

    name = request.form['username']
    password = request.form['password'].encode('utf-8')
    user_id = request.form['user-id']
    age = request.form['age']
    gender = request.form['gender']

    if db_session.query(User).filter_by(id=user_id).first():
        return "duplicate user id"
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
        return "success"


@app.route('/api/users/delete', methods=['POST'])
def rem_user():
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
    db_session = DBSession()

    user_id = request.form['user-id']
    password = request.form['password'].encode('utf-8')

    user = db_session.query(User).filter_by(id=user_id).first()
    user_credentials = db_session.query(AuthStore).filter_by(id=user_id).first()
    db_session.close()
    if user:
        if bcrypt.checkpw(password, user_credentials.phash.encode('utf-8')):
            login_user(user_credentials)
            return "success"
        else:
            return "failed"
    else:
        return "no such user exists"


@login_required
@app.route('/api/test', methods=['POST'])
def test():
    db_session = DBSession()

    user_id = request.form['user-id']
    print(type(user_id))
    user = db_session.query(User).filter_by(id=user_id).first()
    print(user.__dict__)
    if user:
        return str(user.age)
    else:
        return "failed"
