import threading
import pygame
import random
import time

# Array Setup
array1 = []
for x in range(100):
    array1.append(random.randint(1, 100))
array2 = array1.copy()

# pygame setup
FPS = 60
WIDTH, HEIGHT = 1280, 720
WIN = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Algo Race")

# Create a threading event to signal threads to stop
stop_event = threading.Event()

# Sorting Functions
def bubblesort(array):
    for i in range(len(array)):
        if stop_event.is_set():
            break
        for j in range(len(array) - 1, i, -1):
            if stop_event.is_set():
                break
            if array[j] < array[j-1]:
                array[j], array[j-1] = array[j-1], array[j]
            time.sleep(0.01)

def mergesort(array):
    if len(array) > 1:
        mid = len(array) // 2
        left_array = array[:mid]
        right_array = array[mid:]

        # recursion
        mergesort(left_array)
        mergesort(right_array)

        # merge
        i = 0
        j = 0
        k = 0
        while i < len(left_array) and j < len(right_array):
            if left_array[i] < right_array[j]:
                array[k] = left_array[i]
                i += 1
            else:
                array[k] = right_array[j]
                j += 1
            time.sleep(0.03)
            k += 1

        while i < len(left_array):
            array[k] = left_array[i]
            i += 1
            k += 1
            time.sleep(0.01)

        while j < len(right_array):
            array[k] = right_array[j]
            j += 1
            k += 1
            time.sleep(0.01)


def draw_array(array, window, color, location):
    y = 0
    if location == "top":
        for z in array:
            pygame.draw.rect(window, color, pygame.Rect(y, HEIGHT/2 - 3.5*z, WIDTH/len(array), 3.5*z))
            y += WIDTH/len(array)
    elif location == "bottom":
        for z in array:
            pygame.draw.rect(window, color, pygame.Rect(y, HEIGHT - 3.5*z, WIDTH/len(array), 3.5*z))
            y += WIDTH/len(array)

# Main
def draw_window():
    WIN.fill("BLACK")
    draw_array(array1, WIN, "Blue", "top")
    draw_array(array2, WIN, "Red", "bottom")
    pygame.display.update()

def main():
    clock = pygame.time.Clock()
    run = True

    sorting_thread = threading.Thread(target=bubblesort, args=(array1,))
    sorting_thread.start()

    sorting_thread2 = threading.Thread(target=mergesort, args=(array2,))
    sorting_thread2.start()

    while run:
        clock.tick(FPS)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
                stop_event.set()  # Signal threads to stop

        draw_window()

    sorting_thread.join()
    sorting_thread2.join()
    pygame.quit()

main()
