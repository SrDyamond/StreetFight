import tweepy
import logging
from config import create_api
import json
import time
import random

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

array=[]
array.append("Gracias por seguirnos @");
array.append("Lucha por tu clan @");
array.append("Esperamos que disfrutes del juego @");
array.append("Follow por follow @");
array.append("Bienvenid@ @");
array.append("¿Eres tu la leyenda que capturo 20 banderas en un dia? @");
#print (array)


def follow_followers(api):

    logger.info("Retrieving and following followers")
    #Comprobamos nuestros followers
    for follower in tweepy.Cursor(api.followers).items():
        #si nos siguen y no los seguimos,los seguimos y lo mencinamos
        #en un tweet con un mensaje aleatorio de bienvenida
        if not follower.following:
            logger.info(f"Following {follower.name}")
            api.update_status(random.choice(array)+str(follower.name))
            follower.follow()

def main():
    api = create_api()
    while True:
        follow_followers(api)
        logger.info("Waiting...")
        time.sleep(60)

if __name__ == "__main__":
    main()
