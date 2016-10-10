# Robot Localisation Simulator (Final Year Project)
This repo is one of a number from my final year project at university. This in particular is for a piece of software used to simulate two dimensional Monte Carlo localisation. More specific details about the project and the Robot Localisation Simulator in particular can be found below.

<p align="center">
<img src="https://github.com/kevinchar93/University_Project_Particle_Filter_Simulator_App/blob/master/part_filter_out.gif" 
alt="The Complete Robot" width="480" height="345" border="10" />
</p>

### About the Project
My final year project at university was inspired somewhat by my placement year where I worked on programming embedded systems in the form of TV set top boxes, having had this experience I decided to work on a project that dealt with some form of embedded system. In the end I decided to construct and program a robot capable of localising itself in two dimensions.

By the end of the project I'd encountered issues that made me scale down the project. Ultimately I produced a robot that is capable of localising itself in only one dimension, however another piece of software which presents a simple simulation of two dimensional localisation was implemented instead.

The project report titled "[**An Implementation of a Mobile Robot with Localisation Capabilities**](https://github.com/kevinchar93/University_Project_Mobile_Platform_SW/blob/master/An%20Implementation%20of%20a%20Mobile%20Robot%20with%20Localisation%20Capabilities.pdf)" can be found in the root directory of this repo.

In total for the project 4 deliverables were created (links to repos in brackets):
* The actual physical robot
* The software on the robot's embedded platform ([Mobile Platform SW](https://github.com/kevinchar93/University_Project_Mobile_Platform_SW))
* Software used to control the robot remotely & estimate its position ([Grid Localisation App](https://github.com/kevinchar93/University_Project_Grid_Loclisation_App))
* A two dimensional Monte Carlo localisation simulator  ([Robot Localisation Simulator](https://github.com/kevinchar93/University_Project_Particle_Filter_Simulator_App))

## Videos of Robot Localisation Simulator in Action

<p align="center">
<a href="http://www.youtube.com/watch?feature=player_embedded&v=T8QjSjQnkiQ
" target="_blank"><img src="http://img.youtube.com/vi/T8QjSjQnkiQ/0.jpg" 
alt="Zumo Robot in Maze" width="620" height="400" border="10" /></a>
</p>

<p align="center">
<a href="http://www.youtube.com/watch?feature=player_embedded&v=FbZRvst70JM
" target="_blank"><img src="http://img.youtube.com/vi/FbZRvst70JM/0.jpg" 
alt="Zumo Robot in Maze" width="620" height="400" border="10" /></a>
</p>

### More about the Robot Localisation Simulator

An advanced goal of the project was to perform 2D localisation with the physical robot this was not achieved due to issues with configuring an IMU which was to be used for accurate turning, in its place a simulation of 2D localisation was implemented performed in a simplified virtual world.

The two dimensional localisation software makes use of the Monte Carlo localisation method to localize a robot in a simulated environment, as mentioned earlier a simulator was used due to issues with the robot’s turning, but firstly building a simulator would be the natural route to take before implementing the method with a physical robot.

With the software being a simulator realism was a key requirement. Originally the software was going to work by simulating map geometry and the robot would cast rays to detect the distance to said geometry and localise, however this initial implementation of the software uses a simpler model with explicit landmark points in the world that the robot can detect the distance to. The more realistic ray casting model can be implemented in future. Other features that are present to add realism to the simulation are a limit to the range of the landmark sensor as well as Gaussian noise that is added to the movement and measurements that the robot makes just like there would be in the real world.

In the simulation the robot is limited to a world size defined in pixels and can only use landmark points to localise, the distance to these point is calculated using the pythagorean theorem and noise in the robot’s movement and sensing is simulated using Gaussians, the mean being the measurement and the variance being the amount of noise. The simulation is based on a simpler example of Monte Carlo Localisation presented in Udacity Course CS 373 (Georgia Institute of Technology 2013).

The diagram below presents how the Robot Localisation Simulator operates:
<p align="center">
<img src="https://github.com/kevinchar93/University_Project_Particle_Filter_Simulator_App/blob/master/monter_carlo_localisation_flowchart.png" 
alt="Monte Carlo localisation flow chart" width="537" height="555" border="10" />
</p>

The supporting pseudo code below shows how the critical resampling step is performed:
<p align="center">
<img src="https://github.com/kevinchar93/University_Project_Particle_Filter_Simulator_App/blob/master/psuedo_screen.png" 
alt="Resampling step pseudo code" width="642" height="494" border="10" />
</p>

See the report "[**An Implementation of a Mobile Robot with Localisation Capabilities**](https://github.com/kevinchar93/University_Project_Mobile_Platform_SW/blob/master/An%20Implementation%20of%20a%20Mobile%20Robot%20with%20Localisation%20Capabilities.pdf)" for more details.

## License

Copyright © 2016 Kevin Charles

Distributed under the MIT License
