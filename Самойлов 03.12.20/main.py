import pygame as pg
import random as rand
import time
import json
from login_u import reload_Learning_records, write_records
# Вертикальные полосы дорог
exception_up_down_1 = [(100, y) for y in range(0, 350, 50)]
exception_up_down_1.append((100, 350))
exception_up_down_1.extend([(700, 650), (700, 600)])
exception_up_down_2 = [(x, y) for x in range (300, 1000, 200) for y in range(0, 200, 50)]
#Исключения для перекрёстков
exception_for_fork = [(x, y) for x in range(100, 1000, 200) for y in range(400, 850, 150)]

up_down = [(x, y) for y in range(0, 800, 50) for x in range(100, 1000, 200) if (x,y) not in exception_up_down_1 and (x,y) not in exception_up_down_2 and (x,y) not in exception_for_fork and (x,y) not in [(300, 750), (500, 750), (700, 750), (900, 750)]]
up_down.append((300, 200))
up_down.extend([(500, 0), (500, 50), (500, 100), (500, 200)])
up_down_storage = {i: pg.image.load("static/up_down.png") for i in up_down if i not in [(100, 600), (100, 500), (300, 450), (500, 600), (500, 250)]}
print(up_down_storage)
#Перекрёстки
forks = [(100, 700), (100, 550), (100, 400), (300, 700), (300, 550), (300, 400), (300, 150), (500, 700), (500 , 550), (500, 400), (500, 150)]
forks.extend([(700, 550), (700, 400), (700, 150)])
forks_storage = {i: "gray" for i in forks}
# Горизонатльные полосы дорог
horizontal_1 = [(x, 700) for x in range(100, 550, 50)]
horizontal_2 = [(x, 550) for x in range(100, 750, 50) if (x,550)!=(700,550)]
horizontal_3 = [(x, 400) for x in range(0, 750, 50) if (x, 400)!= (700, 400)]
horizontal_4 = [(x, 150) for x in (300, 750, 50) if (x, 150) != (700, 150)]
horizontal = horizontal_1 + horizontal_2 + horizontal_3 + horizontal_4
horizontal_signs_exceptions = [(650, 400)]
horizontal_sings_storage = {(650, 400): pg.image.load("static/Wbrick_horizontal.png")}
horizontal.extend([(x, 150) for x in range(350, 650) if (x, 150) !=(500, 150)])
#Словарь соответствия координат горизонтальных дорог их изображению + отсечение перекрёстков
horizontal_storage = {i: pg.image.load("static/left_right.png") for i in horizontal if i not in forks and i not in [(750, 150), (50, 150), (650, 400)]}
print(horizontal_storage)

#Находим координаты знаков "Стоп"
vertical_signs = [(100, 600), (100, 500), (300, 450), (500, 600), (500, 250)]
vartical_signs_storage = {i: pg.image.load("static/Wbrick_vertical.png") for i in vertical_signs}


crosses_with_out_blocks ={(100, 700+200): {1:10, 2:5}, (700, 150+200): {1:5, 2: 10}, (300, 550+200): {1: 5, 2:10}, (500, 550+200): {1: 10, 2:0}, (500, 400+200): {1:10, 4:5}, (300, 400+200): {1: 10, 4:5}, (300, 150): {2: 10}, (500, 150): {1: 50, 2: -10}}
coding = {i: [tuple_name, crosses_with_out_blocks[tuple_name]] for i, tuple_name in enumerate(crosses_with_out_blocks.keys())}
with open("q_val.txt", "w", encoding="utf-8") as file:
    file.write(json.dumps(coding))
class MainArea():
    def __init__(self, up_down_storage, horizontal_storage, forks_storage, vertical_signs_storage, horizontal_signs_storage, for_q_values):
        pg.init()
        self.cars = {1: "static/Wcar.png", 2: "static/WcarRight.png", 3: "static/WcarBottom.png", 4:"static/WcarLeft.png"}
        #Направление движения: 1- вверх, 2-вправо, 3-вниз, 4-влево
        self.start_img = pg.image.load("static/Wenter.png")
        self.axis = 1
        self.screen = pg.display.set_mode((800, 1000))
        self.background = pg.Surface((800, 800))
        self.x_y = (100, 950)
        self.car = pg.image.load("static/Wcar.png")
        self.up_down_storage = up_down_storage
        self.horizontal_storage = horizontal_storage
        self.forks_storage = forks_storage
        self.vertical_signs_storage = vertical_signs_storage
        self.horizontal_sings_storage = horizontal_signs_storage
        self.parking = pg.image.load("static/Wparking.png")
        #Словарь с координатами поворотов и их порядковым номером на трассе
        self.for_q_values = for_q_values
        self.q_val = self.read_q_val()
        #Кнопки
        self.stop_button = pg.image.load("static/Стоп.png")
        self.start_button = pg.image.load("static/start.png")
        self.test_drive_button = pg.image.load("static/test.png")
        self.pause_button = pg.image.load("static/pause.png")
        self.study_start_button = pg.image.load("static/study_start.png")
        self.study_stop_button = pg.image.load("static/study_stop.png")
        #Коэффициент обучения
        self.lr = 0.6
        #History_of_learning
        self.history = {}
    def read_q_val(self):
        with open("q_val.txt", "r", encoding="utf-8") as file:
            q_val = file.read()
            q_val = json.loads(q_val)
        return q_val

    def rewrite_q_val(self, new_q_dict):
        with open("q_val.txt", "w", encoding="utf-8") as file:
            file.write(json.dumps(new_q_dict))


    def do_step_run(self):
        print("TEST")
        time_ride = 0
        for i in range(3):
            self.x_y = (self.x_y[0], self.x_y[1] - 50)
            print(self.x_y)
            for key, value in self.horizontal_storage.items():
                self.background.blit(value, key)
            for key, value in self.up_down_storage.items():
                self.background.blit(value, key)
            for key, value in self.forks_storage.items():
                pg.draw.rect(self.background, pg.Color(value), (key[0], key[1], 50, 50))
            for key, value in self.vertical_signs_storage.items():
                self.background.blit(value, key)
            for key, value in self.horizontal_sings_storage.items():
                self.background.blit(value, key)
            # Смещаем задний фон вниз на 200 пикселей для выделения места кнопкам
            self.screen.blit(self.background, (0, 200))
            # Размещаем автомобиль на начальную точку
            self.screen.blit(self.car, self.x_y)
            self.screen.blit(self.start_button, (0, 0))
            self.screen.blit(self.stop_button, (100, 0))
            self.screen.blit(self.test_drive_button, (200, 0))
            self.screen.blit(self.parking, (500, 200))
            pg.display.update()
            time.sleep(1)
            time_ride+=1
        #Обновление в таблице Records
        reload_Learning_records(time_ride=time_ride, number_of_turns=1, is_exit=False)
    def check_best_act(self, i):
        q_val = self.read_q_val()
        best_act = list(dict(sorted(q_val[str(i)][1].items(), key=lambda item: item[1])).keys())[0]
        return best_act

    def start_study(self):
        self.x_y = (self.x_y[0], self.x_y[1]-50)
        print(self.x_y)
        self.update()
        for i in range(8):
            best_act = self.check_best_act(i)
            print(best_act)
            self.x_y = (self.x_y[0]+50, self.x_y[1])
            if self.axis == 1 and best_act == "2":
                self.car = pg.image.load("static/WcarRight.png")
                self.update()
                while (self.x_y[0], self.x_y[1]) not in list(self.vertical_signs_storage.keys()) and (self.x_y[0], self.x_y[1]) not in list(self.forks_storage.keys()) and (self.x_y[0], self.x_y[1]) not in list(self.horizontal_sings_storage.keys()):
                    self.x_y = (self.x_y[0]+50, self.x_y[1])
                    self.update()
                    time.sleep(5)
                print(list(self.vertical_signs_storage.keys()))
                if (self.x_y[0], self.x_y[1]) in list(self.vertical_signs_storage.keys()):
                    q_val = self.read_q_val()
                    reward = -5
                    q_val[str(i)][1][best_act] = (1 - self.lr) * q_val[str(i)][1][best_act] + self.lr*reward
                    self.rewrite_q_val(q_val)
                    self.x_y = (100, 750)
                    return self.start_study()
                elif (self.x_y[0], self.x_y[1]) in list(self.vertical_signs_storage.keys()):
                    q_val = self.read_q_val()
                    reward = 5
                    q_val[str(i)][1][best_act] = (1 - self.lr) * q_val[str(i)][1][best_act] + self.lr * reward
                    self.rewrite_q_val(q_val)
        return None

    def stop_study(self):
        pass

    def check_buttons(self, for_what=False):
        for ev in pg.event.get():

            if ev.type == pg.QUIT:
                pg.quit()
                # checks if a mouse is clicked
            if ev.type == pg.MOUSEBUTTONDOWN:
                mouse = pg.mouse.get_pos()
                print(mouse)
                if mouse[0] < 100 and mouse[0] > 0 and mouse[1] < 30 and mouse[1] > 0:
                    self.do_step_run()
                elif mouse[0] > 90 and mouse[0] < 200 and mouse[1] < 30 and mouse[1] > 0:
                    if for_what=="test":
                        self.x_y = (100, 950)
                        self.axis = 1
                        self.car = pg.image.load(self.cars[1])
                        break
                    self.x_y = (100, 950)
                    self.axis = 1
                    self.car = pg.image.load(self.cars[1])
                elif mouse[0] > 180 and mouse[0] < 300 and mouse[1] < 30 and mouse[1] > 0:
                    self.test_drive()
                elif mouse[0] > 290 and mouse[0] < 400 and mouse[1] < 30 and mouse[1] > 0:
                    pass


                elif mouse[0] > 490 and mouse[0] < 600 and mouse[1] < 30 and mouse[1] > 0:
                    print("TEST")
                    study = self.start_study()
                    if study:
                        self.start_study()


                elif mouse[0] > 590 and mouse[0] < 700 and mouse[1] < 30 and mouse[1] > 0:
                    self.stop_study()



    def update(self):
        for key, value in self.horizontal_storage.items():
            self.background.blit(value, key)
        for key, value in self.up_down_storage.items():
            self.background.blit(value, key)
        for key, value in self.forks_storage.items():
            pg.draw.rect(self.background, pg.Color(value), (key[0], key[1], 50, 50))
        for key, value in self.vertical_signs_storage.items():
            self.background.blit(value, key)
        for key, value in self.horizontal_sings_storage.items():
            self.background.blit(value, key)
            # Смещаем задний фон вниз на 200 пикселей для выделения места кнопкам
        self.screen.blit(self.background, (0, 200))
        # Размещаем автомобиль на начальную точку
        self.screen.blit(self.start_img, (100, 950))
        self.screen.blit(self.car, self.x_y)
        self.screen.blit(self.start_button, (0, 0))
        self.screen.blit(self.stop_button, (100, 0))
        self.screen.blit(self.test_drive_button, (200, 0))
        self.screen.blit(self.pause_button, (300, 0))
        self.screen.blit(self.parking, (400, 200))
        self.screen.blit(self.study_start_button, (500, 0))
        self.screen.blit(self.study_stop_button, (600, 0))
        pg.display.update()
        time.sleep(1)


    def test_drive(self):
        time_ride = time.time()
        self.x_y = (self.x_y[0], self.x_y[1] - 50)
        self.update()
        self.axis = 2
        self.car = pg.image.load(self.cars[2])
        self.update()
        self.x_y = (self.x_y[0] + 50 , self.x_y[1])
        self.update()
        for i in range(3):
            self.x_y = (self.x_y[0] + 50, self.x_y[1])
            self.update()
        self.axis = 4
        self.car = pg.image.load(self.cars[4])
        self.update()
        for i in range(4):
            self.x_y = (self.x_y[0] - 50, self.x_y[1])
            self.update()
        self.axis = 3
        self.car = pg.image.load("static/WcarBottom.png")
        self.update()
        self.x_y = (self.x_y[0], self.x_y[1] +50)
        time_ride = time.time() - time_ride
        reload_Learning_records(time_of_ride=time_ride, number_of_turns=3, is_exit=False)

    def go_to_nearest_cross(self, i):
        # while self.x_y not in
        pass
    def do_study(self):
        for i in range(8):
            pass
            # self.

    def update_Q_values(self):
        pass




    def create_area(self):
        #Закрашиваем задний фон в белый цвет для видимости png-изображений
        self.background.fill((255, 255, 255))
        self.update()
        pg.display.update()
        clock = pg.time.Clock()
        while True:
            self.check_buttons()
            self.update()
            pg.display.update()

        clock.tick(1)
        pg.quit()



area = MainArea(up_down_storage, horizontal_storage, forks_storage, vartical_signs_storage, horizontal_sings_storage, for_q_values=crosses_with_out_blocks)

area.create_area()