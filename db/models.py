from sqlalchemy import Column, Integer, String, ForeignKey, ForeignKeyConstraint, Boolean, ARRAY
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class User(Base):
    __tablename__ = 'users'

    name = Column(String(80), nullable=False)
    id = Column(String(20), primary_key=True)
    gender = Column(String(20))
    age = Column(Integer)
    encodings = relationship('Encoding')


class Encoding(Base):
    __tablename__ = 'encodings'

    user_id = Column(String(20), ForeignKey('users.id'), primary_key=True)
    encoding = Column(String(100))

    # TODO: Restrict this to five
    encoding_count = Column(Integer)


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
