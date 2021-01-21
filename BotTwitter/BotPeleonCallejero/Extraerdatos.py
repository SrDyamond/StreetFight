import tweepy
import logging
from config import create_api
from controlador_menciones import parse_text
import time

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()


# Function to extract tweets
def get_tweets(api):


	mentions = api.mentions_timeline(count=5)
	logger.info("Revisando menciones")
	for mention in mentions:
		parse_text(mention.text,mention.user.screen_name,api)

	#FOLLOWFORFOLLOW AND SEND RANDOM WELCOME MESSAGE
	logger.info("Revisando followers")
	for follower in tweepy.Cursor(api.followers).items():
	#si nos siguen y no los seguimos,los seguimos y lo mencinamos
	#en un tweet con un mensaje aleatorio de bienvenida
		if not follower.following:
			logger.info(f"Following {follower.screen_name}")
			api.update_status("Hola @"+str(follower.screen_name)+"!Gracias por darnos follow :D.\nPara saber los comandos que acepto escribe <Ayuda!>")
			follower.follow()

def main():
	api = create_api()
	while True:
		get_tweets(api)
		logger.info("Estoy dormido...ZzZzZzZ")
		time.sleep(60)

if __name__ == "__main__":
	main()
