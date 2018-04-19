function main(args) {
  console.log('Processing Slack Command :' + args);

  if (args.token != args.slackVerificationToken) {
    return {
      statusCode: 401
    }
  }

  var command = {
    user_id: args.user_id,
    response_url: args.response_url,
    text: args.text
  }

  var siteRegEx = /^site\??/g;
  var evanRegEx = /^evangelists\??/g;

  if (command.text.match(siteRegEx)) {
    return {
      "text": "developers.redhat.com"
    };
  } else if (command.text.match(evanRegEx)) {
    return {
      "attachments": [
        {
          "author_name": "Burr Sutter",
          "author_link": "https://twitter.com/burrsutter",
          "color": "good"
        },
        {
          "author_name": "Don Schenck",
          "author_link": "https://twitter.com/donschenck",
          "color": "#439FE0"
        },
        {
          "author_name": "Edson Yanaga",
          "author_link": "https://twitter.com/yanaga",
          "color": "#638FF0"
        },
        {
          "author_name": "Rafael Benevides",
          "author_link": "https://twitter.com/rafabene",
          "color": "#433EF0"
        },
        {
          "author_name": "Kamesh Sampath",
          "author_link": "https://twitter.com/kamesh_sampath",
          "color": "danger"
        }
      ]
    }
  } else {
    return {
      "text": "Learn more.Code more.Share more"
    }
  }

}