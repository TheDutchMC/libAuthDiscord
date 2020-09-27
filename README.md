# libAuthDiscord

A library for other plugins to hook into. Also allows to whitelist your Minecraft server based on Discord roles in a Guild

## Setup

1) Enable Developer mode on discord
> **Note:** Discord has a guide on this [here](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-)
2) Create a Bot [here](https://discord.com/developers) and invite the bot to your server.
3) Copy the bot token (found under the `Bot` section of your Bot application) into the config.yml of LibAuthDiscord
4) Right click your server icon in discord and select `copy id`, this is your guild ID, and paste this into the config.yml too.
5) To get a role ID, assign the role to a user, click their name and then right click on the role and select `copy ID`. In the config it should be set as follows:
```yml
permittedRoles:
- <role id 1>
- <role id 2>
etc
```

### Support
My discord server for support can be found here: https://discord.gg/xE3FcGj

Hope this helps!
