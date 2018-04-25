from sqlalchemy import Column, Integer, String, ForeignKey, ForeignKeyConstraint, Boolean, Float
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class User(Base):
    __tablename__ = 'users'

    name = Column(String(80), nullable=False)
    id = Column(String(20), primary_key=True)
    gender = Column(String(20))
    age = Column(Integer)
    missing_encodings = relationship('MissingImageEncoding')
    found_encodings =  relationship('FoundImageEncoding')


class MissingImageEncoding(Base):
    __tablename__ = 'missing_encodings'

    id = Column(String(20), ForeignKey('users.id'), primary_key=True)
    encoding_count = Column(Integer, primary_key=True)
    encoding = Column(String)

class FoundImageEncoding(Base):
    __tablename__ = 'found_encodings'

    id = Column(String(20), ForeignKey('users.id'), primary_key=True)
    encoding_count = Column(Integer, primary_key=True)
    encoding = Column(String)

class ImageDetailsFound(Base):
    __tablename__ = 'image_details_found'

    id = Column(String(20), ForeignKey('users.id'), primary_key=True)
    encoding_count = Column(Integer, primary_key=True)
    name = Column(String(80), nullable=False)
    gender = Column(String(20))
    age = Column(Integer)
    latitude = Column(String(80))
    longitude = Column(String(80))
    phoneNo = Column(String(20))

class ImageDetailsMissing(Base):
    __tablename__ = 'image_details_missing'

    id = Column(String(20), ForeignKey('users.id'), primary_key=True)
    encoding_count = Column(Integer, primary_key=True)
    name = Column(String(80), nullable=False)
    gender = Column(String(20))
    age = Column(Integer)
    latitude = Column(String(80))
    longitude = Column(String(80))
    phoneNo = Column(String(20))

class UserCount(Base):
    __tablename__='usercount'

    id = Column(String(20), ForeignKey('users.id'), primary_key=True)
    missing_count = Column(Integer)
    found_count = Column(Integer)

class AuthStore(Base):
    __tablename__ = 'authstore'

    id = Column(String(10), primary_key=True)
    salt = Column(String(50))
    phash = Column(String(50))
    isAdmin = Column(Boolean())

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self.id.encode('utf-8')


class Admin(Base):
    __tablename__ = 'admins'

    id = Column(String(10), primary_key=True)
    name = Column(String(80), nullable=False)
    gender = Column(String(20))
    isSuper = Column(Boolean())
