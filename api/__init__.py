from flask import Flask
from flask_login import LoginManager
from db.models import *
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Environment variables for uploads
UPLOAD_FOLDER = './api/static/img'
ALLOWED_EXTENSIONS = {'jpg', 'jpeg', 'png'}

# Flask App initialization
app = Flask(__name__)
app.secret_key = 'secret_key'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# Final Configuration depending upon sample version
engine = create_engine('sqlite:///app.db')
Base.metadata.create_all(engine)
DBSession = sessionmaker(bind=engine)

"""
The login manager contains the code that lets your application and Flask-Login work together,
such as how to load a user from an ID, where to send users when they need to log in etc.
"""
login_manager = LoginManager(app)
login_manager.init_app(app)

# Login Manager view definition
login_manager.login_view = "authenticate"

@login_manager.user_loader
def load_user(user_id):
    db_session = DBSession()
    user_from_auth = db_session.query(AuthStore).filter_by(id=str(user_id)).first()
    db_session.close()
    return user_from_auth


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


from api import views
