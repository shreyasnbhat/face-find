from db.models import *
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from main import get_encoding, face_recognition
import os,bcrypt
from shutil import copyfile
import json

engine = create_engine('sqlite:///app.db')
Base.metadata.create_all(engine)
DBSession = sessionmaker(bind=engine)

path = '/home/shreyas/Desktop/DB'

db_session = DBSession()

data = {}

def add_data(name,lat,lng,age,gender):
    global data
    data[name] = {'latitude':lat,
                   'longitude':lng,
                   'age':age,
                   'gender':gender}

def add_user(name,password,user_id):

    if db_session.query(User).filter_by(id=user_id).first():
        return "Duplicate User ID"
    else:
        db_session.add(User(id=user_id,
                            name=name,
                            gender='Male',
                            age=28))

        salt = bcrypt.gensalt()
        phash = bcrypt.hashpw(password, salt)
        db_session.add(AuthStore(id=user_id,
                                 phash=phash,
                                 salt=salt,
                                 isAdmin=True))

        db_session.commit()
        print("User Added",name,password)

def add_image(user_id,count,name,path):
    encodings = get_encoding(path)
    encoding_str = json.dumps(list(encodings))
    db_session.add(MissingImageEncoding(id=user_id,encoding=encoding_str,encoding_count=count))
    db_session.add(ImageDetailsMissing(id = user_id, encoding_count=count,
                                       name=name, age=data[name]['age'], gender =data[name]['gender'],
                                        latitude=data[name]['latitude'], longitude =data[name]['longitude'],phoneNo ='9757275331'))
    db_session.commit()

def add_counts(user_id):
    db_session.add(UserCount(id=user_id, missing_count=5, found_count=0))
    db_session.commit()


if __name__ == '__main__':
    add_data('Kevin Spacey','18.99860','-99.32721',54,'Male')
    add_data('Matt Le Blanc','0.46817','109.52831',62,'Male')
    add_data('Kaley Cuoco','18.99860','-99.32721',37,'Female')
    add_data('Robert Downey','18.99860','-99.32721',54,'Male')
    add_data('Benedict Cumberbatch','18.99860','-99.32721',42,'Male')
    add_data('Tom Holland','18.99860','-99.32721',20,'Male')
    add_data('Chloe Bennet','18.99860','-99.32721',31,'Female')

    user_ids = ['axelrod','chuck','coulson','penny','richard','vader','wayne']
    users = ['Bobby Axelrod','Chuck Rhoades','Philip J Coulson','Penny','Richard Hendricks','Anakin Skywalker','Bruce Wayne']
    password = ('1234').encode('utf-8')

    name_to_id = {}

    for i in range(len(user_ids)):
        name_to_id[users[i]] = user_ids[i]

    for i in range(len(user_ids)):
        add_user(users[i],password,user_ids[i])

    user = 0
    for i in os.listdir(path):
        count = 0
        user_name = i.strip()
        for j in os.listdir(path + '/' + i):
            count+=1
            upload_path = '/home/shreyas/Code/face-find/api/static/img/' + name_to_id[users[user]] + '_M' + str(count) + '.jpg'
            copyfile(path + '/' + i + '/' + j,upload_path)
            print(users[user],user_name,count,name_to_id[users[user]],upload_path)
            add_image(name_to_id[users[user]],count,user_name,upload_path)

        user+=1

    for i in user_ids:
        add_counts(i)    
