#!/bin/bash

sm -f --start MONGO DATASTREAM EMAIL HMRC_EMAIL_RENDERER MAILGUN_STUB ASSETS_FRONTEND DESKPRO_STUB HMRCDESKPRO

sm --start API_GATEWAY_STUB THIRD_PARTY_APPLICATION THIRD_PARTY_DEVELOPER API_SUBSCRIPTION_FIELDS API_DEFINITION

./run_local.sh