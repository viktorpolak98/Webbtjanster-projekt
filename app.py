from flask import Flask, render_template, request, redirect, session, flash, url_for
import json

import requests
import tweepy



# # response = requests.get("https://polisen.se/api/events?locationname=Göteborg;Angered")
# # # response.json()
# # # json(response.text)
# # res = json.loads(response.text)
# # print(res[0])
# # # list1 = []
# # # list1.append(response)
# # # print(list1)
# # # things = json.loads(response)
# # # print(things)
# # print(response.status_code)
# # print(response.text)
# q = input("sök ")
# response = request.get("http://polisen.se/api/events?locationname=" + q)
# print(response.text)

app = Flask(__name__)
app.config["DEBUG"] = True
@app.route('/')
def hello():

    return render_template('index.html')

@app.route('/search', methods=['GET', 'POST'])
def search():
    brott = request.form['brott']

    response = requests.get("https://polisen.se//api/events?type=" + brott)
    print(response.headers['content-type'])
    # f=open(response)
    # data=json.load(f)
    res = response.json()
    # print(res)

    for stories in res:
        a_dict = stories["location"]
        for things in a_dict:
            a_list = a_dict["gps"].split(",")
            longitude = a_list[0]
            latitude = a_list[1]

    auth = tweepy.OAuthHandler("S0yoM96xmJnglJeZJz2biErzO", "RnEjSRwvj1ACxDpJgKoyo1JMf0GDrZ65k4KLh5foKekBNIpkIr")
    auth.set_access_token("1338824296177786883-T9QObu6entzSrcT0oNkhPyFj9xcxTL", "q51wL0jCdrOe0vcZXZH8wfY2jKNCprNGwvDSVRyo080wL")
    api = tweepy.API(auth)

    tweeters = []

    for tweet in api.search(geocode=str(longitude + "," +  latitude + "," + "10km"), lang="sv", rpp=1):
        # print(f"{tweet.user.name}:{tweet.text}")
        tweeters.append(tweet.text)
    print(tweeters)
    
    return render_template('search.html', brott=brott, res=res, tweeters=tweeters)
if __name__ == '__main__':
    app.debug = True
    app.run(host='localhost', port=8080, debug=True)