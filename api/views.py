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

    username = request.form['username']
    password = request.form['password'].encode('utf-8')
    user_id = str(random.randint(0,100000))
    age = request.form['age']
    gender = request.form['gender']

    db_session.add(User(id=user_id,
                        name=username,
                        gender=gender,
                        age=age))

    salt = bcrypt.gensalt()
    phash = bcrypt.hashpw(password, salt)
    db_session.add(AuthStore(id=user_id,
                             phash=phash,
                             salt=salt,
                             isAdmin=True))
    db_session.commit()
    return json.dumps("{'message':'success'}")