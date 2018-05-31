def main(params):
   print "Py msgaction params: " + str(params)
   for key in params["body"]:
        print key, params["body"][key]        
   return { "payload" : 
	"Python on OpenWhisk on OpenShift " + str(params["body"]["text"])}