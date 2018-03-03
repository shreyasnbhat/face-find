from flask import Flask

# Environment variables for uploads
UPLOAD_FOLDER = '/home/shreyas/Code/face-find/app/static/img/unknown'
ALLOWED_EXTENSIONS = {'jpg', 'png'}

# Flask App initialization
app = Flask(__name__)
app.secret_key = 'secret_key'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


from app import views
