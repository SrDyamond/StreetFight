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
# Function to extract tweets
def get_tweets(api):


	mentions = api.mentions_timeline(count=5)
	logger.info("Revisando menciones")
	for mention in mentions:
		try:
			if (mention.text == "@StreetFightSP Funcionas?"):
				api.update_status("Soy un buen bot, mi creador es un genio @"+str(mention.user.screen_name))
				print ("Mensaje enviado a "+str(mention.user.screen_name))
		except tweepy.TweepError as error:
		    if error.api_code == 187:
			    print('duplicate message')

	#Comprobamos nuestros followers
	logger.info("Revisando followers")
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
		get_tweets(api)
		logger.info("Dormido...ZzZzZzZ")
		time.sleep(60)

if __name__ == "__main__":
	main()
