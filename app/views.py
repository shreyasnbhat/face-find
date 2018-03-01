from flask import render_template, request, redirect, url_for, abort, flash, session, g, send_from_directory
import os
from werkzeug.utils import secure_filename
from app import *
from main import pre_process, compute
import subprocess as s


@app.route('/', defaults={'filename': None})
@app.route('/<string:filename>')
def home(filename):
    if request.method == 'GET':
        if filename is not None:
            labels = compute(filename)
            print(labels)
            return render_template('index.html', labels=labels)
        else:
            return render_template('index.html')


@app.route('/upload', methods=['POST', 'GET'])
def upload():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']

        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            print("Done!")
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return redirect(url_for('home', filename=filename))


@app.route('/clear', methods=['GET', 'POST'])
def clear_encodings():
    s.call(['rm', 'encodings.npy'])
    s.call(['rm', 'labels.npy'])
    return redirect(url_for('home'))

@app.route('/populate',methods=['GET','POST'])
def populate_encodings():
    pre_process()
    return redirect(url_for('home'))
