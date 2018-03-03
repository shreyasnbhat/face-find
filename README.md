# Face Finder
An Android App to help find missing people

## Components
- A Flask Server to do heavy computations.
- An Android App(portable device)

### Notes
- `app/static/` must contain a directory called `img/` which is further going to have 2 sub-directories called `known` and `unknown`
- `encodings.npy` and `lables.npy` as preprocessed numpy arrays which can be computed by calling the `/populate` endpoint
- Use `/clear` to remove prexisting encodings. In future need to add a SQLite DB
- UPLOAD_FOLDER is `app/static/img/unknown/`

### Setup
- Install virtualenv `pip3 install virtualenv`
- Create a new python3 environment by `virtualenv -p python3 face-find` 
- Activate the python3 virtualenv with `source face-find/bin/activate`
- Install requirements with `pip3 -r requirements.txt`
- Start flask server locally by  `python3 run.py d 5000`

### Libraries Used
- flask
- face_recognition
- dlib
- numpy
