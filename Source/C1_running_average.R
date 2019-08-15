

#-- !!!!! you have to enter arguments window, step  !!!!

#takes as inout a file containing numbers and outputs the 3 first moments over a sliding window
# with a step 

#####list of packages to use

library(moments)  #for using moments functions
#library(stringr) # for str_count
#library(stringi)  #for generating symbols
library(evobiR)   # for slidingWindow






#--------------------modified sliding window  -----------
SlidingWindow2 <- function(FUN, data, window, step, strict=F, orders){
  # Validation testing
  if(strict) {
    
    if(!is.numeric(data)) stop("Please supply numeric data")
    
    if(sum(is.vector(data), is.matrix(data)) == 0) {
      stop("You must supply data as a vector or matrix")
    }
    
    if(is.vector(data)){
      if(window > length(data)) stop("Window size is too large")
      if((step + window) > length(data)) stop("Window and step size 
                                            should be small enough 
                                            that multiple windows 
                                            can be examined")
    }
    
    if(is.matrix(data)){
      if(window > nrow(data)) stop("Window size is too large for number of rows")
      if(window > ncol(data)) stop("Window size is too large for number of cols")
      if((step + window) > nrow(data)) stop("Window and step size 
                                            should be small enough 
                                            that multiple windows 
                                            can be examined")
    }
  }
  
  # code for vectors
  if(is.vector(data)) {
    total <- length(data)
    spots <- seq(from = 1, to = (total - window + 1), by = step)
    result <- vector(length = length(spots))
    for(i in 1:length(spots)){
      result[i] <- match.fun(FUN)(data[spots[i]:(spots[i] + window - 1)], orders)
      
    }
  }
  
  # code for matrices
  if(is.matrix(data)){
    total.x <- ncol(data)
    spots.x <- seq(from = 1, to = (total.x - window + 1), by = step)
    total.y <- nrow(data)
    spots.y <- seq(from = 1, to = (total.y - window + 1), by = step)
    result <- matrix(length(spots.y), length(spots.x))
    for(i in 1:length(spots.y)){
      for(j in 1:length(spots.x)){
        result[i, j] <- match.fun(FUN)(data[spots.y[i]:(spots.y[i] + window - 1),
                                            spots.x[j]:(spots.x[j] + window - 1)])
      }
    }
  }
  
  # complete failure message
  if(!exists("result")) stop("Hmmm unknown error... Sorry")
  
  # return the result to the user
  return(result)
}

#############################################################

#------------------pass a file as argument -------------

args= commandArgs(trailingOnly = TRUE) # receive args
data_list= read.csv(args[1], header= TRUE) #read from csv


data=unlist(data_list) # transform args from data frame to numeric


#---------- The function that returns the 3 moments of the events on a sliding window


  
  window= as.integer(args[2]) # transform args from data frame to integer
  step= as.integer(args[3])
  
result= SlidingWindow2("moment", data, window, step,orders=1)

#-------- second part


###########
###     ###
###      ##
###     ###
###########

   ### 
   ###
 #######
  #####
   ###
    #



P=30
d=20



cube_6=cube_3=array(dim = length(result))


for (i in 1:length(result)) {
  cube_3[i]= abs(result[i]-P);
  if(cube_3[i]>d)
  { cube_6[i]= TRUE}
  else{cube_6[i]=FALSE}
}


cube_6
