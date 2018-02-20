# ch-058, geocitizen

___build and deploy (ubuntu16, git2, maven3, tomcat9)___

1) in config file [`~/Ch-058/src/main/resources/application.properties`](https://git.io/vA4Sw)
	you might want to edit following properties
	 * [`front.url`](https://git.io/vARyB) - front url
	 * [`db.url`](https://git.io/vARyu) - db uri (__db must be created manually__)
	 * [`db.username`](https://git.io/vARyo) & [`db.password`](https://git.io/vARyK) - db credentials
1) `git clone https://github.com/nromanen/Ch-058.git; cd Ch-058`
1) `mvn install && mv target/citizen.war /usr/share/tomcat9/webapps/ && /usr/share/tomcat9/bin/startup.sh`
1) e.g. <http://localhost:8080/citizen/>

=================================================================================================================================

if you want to make changes to frontend 
you have to cd to `~/Ch-058/front-end` dir and run `npm run dev` after successful execution you'll see url.
to generate the production build you have to
 - change in `~/Ch-058/src/main/webapp/index.html`([GitHub](git.io/vA49U)) line `export const backEndUrl =` and replace url with tomcat's address (e.g. `'http://localhost:8080/citizen'`)
 - run `npm run build`, move all files from `~/Ch-058/front-end/dist` to `~/Ch-058/src/main/webapp`
 - in `~/Ch-058/src/main/webapp/index.html` put dots on lines
                                   - ([github](https://git.io/vARrw)) after `<link href=` 
                                   - ([github](https://git.io/vARr5)) after `<script type=text/javascript src=`        
- then repeat 3rd step of `build and deploy`      
___other___
[swagger](http://localhost:8080/citizen/swagger-ui.html)

[heroku](https://geocitizen.herokuapp.com)  
  

