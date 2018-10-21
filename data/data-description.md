# Data Description

The data is a CSV containing individual page views on our various websites. The column definitions are as follows:

- `anonymous_user_id` - unique identifier for a visitor to the website
- `url` - the full URL of the page
- `time` - the Unix time of the page view
- `browser` - user's web browser
- `os` - user's operating system
- `screen_resolution` - the user's screen resolution in pixels

## URL Parameters

There are a few URL parameters of interest. URL parameters start after the `?` in the URL and are separated by the `&` character.

We use standard UTM parameters to denote the source of traffic from marketing activities. Parameters prefixed with utm are considered information about the marketing activity that generated the traffic. In particular:

- `utm_source` indicates the source of the traffic, e.g. Google, Facebook
- `utm_medium` indicates the medium of the traffic, e.g. search, display (banner ads) or social
- `utm_campaign` indicates the campaign

For more information on UTM parameters, you may like to read https://www.semrush.com/blog/use-utm-tracking-codes-google-analytics/

## URL Structure

URLs in the dataset are standard, and consist of the following parts:

- `protocol`
- `host`
- `port`
- `path`
- `query`
- `fragment`

Not all parts may be present for all URLs. For more information on parsing URLs, see: https://prestodb.io/docs/current/functions/url.html