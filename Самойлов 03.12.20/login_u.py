from sqlalchemy import Column, Integer, String, Boolean, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, Session, backref
from sqlalchemy.exc import IntegrityError

#Создём движок подключения к БД
engine = create_engine("sqlite:///infodatabase.db", echo=True)
#Объявляем слой реализации декларативного создания БД
Base = declarative_base(bind=engine)
class Records(Base):
    __tablename__ = "Records"
    number_of_ride = Column(Integer, primary_key=True)
    time_of_ride = Column(String(120), nullable=False)
    number_of_turns = Column(Integer, nullable=False)
    is_exit = Column(Boolean, nullable=False)



class Learning_Records(Base):
    __tablename__ = "Learning_records"
    number_of_iterarion = Column(Integer, primary_key=True)
    time_of_ride = Column(String(120), nullable=False)
    number_of_turns = Column(Integer, nullable=False)
    is_exit = Column(Boolean, nullable=False)

#Создаём таблицы по вышеописанному шаблону
Base.metadata.create_all()


def reload_Learning_records(time_of_ride, number_of_turns, is_exit):
    engine = create_engine("sqlite:///infodatabase.db", echo=True)
    session = Session(bind=engine)
    learning_records = session.query(Learning_Records).all()
    learning_records.append(Learning_Records(time_of_ride=time_of_ride, number_of_turns=number_of_turns, is_exit=is_exit))
    session.commit()
    session.close()

def write_records(time_of_ride, number_of_turns, is_exit):
    engine = create_engine("sqlite:///infodatabase.db", echo=True)
    session = Session(bind=engine)
    records = session.query(Learning_Records).all()
    records.append(Records(time_of_ride=time_of_ride, number_of_turns=number_of_turns, is_exit=is_exit))
    session.commit()
    session.close()
