from flask import Flask, render_template, request, redirect, session, flash, url_for
import json

import requests


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
    ort = request.form['ort']

    response = requests.get("https://polisen.se/api/events?locationname=" + ort)
    print(response.headers['content-type'])
    # f=open(response)
    # data=json.load(f)
    res = response.json()
    # print(res)
    for stories in res:
        print(stories["name"])

    
    


    return render_template('search.html', ort=ort, res=res)

if __name__ == '__main__':
    app.debug = True
    app.run(host='localhost', port=8080, debug=True)