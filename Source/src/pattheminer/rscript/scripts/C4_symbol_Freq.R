

#-- !!!!! you have to enter arguments window, step !!!!
#takes as input a file of 'a' and 'b' and returns the frequency of each over a window and with a step

################## list of packages to use

library(moments)  #for using moments functions
#library(stringr) # for str_count
library(stringi)  #for generating symbols
library(evobiR)   # for slidingWindow

########################
##   ##   ##   ##   ##
###    ###    ###   ###
########################
#----------------- function sliding window modified ----------

SlidingWindow3 = function(FUN, data, window, step, strict=F, char){
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
      result[i] <- match.fun(FUN)(data[spots[i]:(spots[i] + window - 1)], char)
      
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
                                            spots.x[j]:(spots.x[j] + window - 1)],char)
      }
    }
  }
  
  # complete failure message
  if(!exists("result")) stop("Hmmm unknown error... Sorry")
  
  # return the result to the user
  return(result)
}

###################################################################




#---------- pass a file as argument ----------

args= commandArgs(trailingOnly = TRUE)
str_var= read.csv(args[1], header= TRUE, stringsAsFactors = FALSE)



str_var=unlist(str_var)        #use unlist to transform data from datafrane to numerical vectors
str_var= as.character(str_var) # to  change the type of data from 'Factor' to char

window=as.integer(args[2]) # transform args from data frame to integer
step=as.integer(args[3])


     ### 
     ###
   #######
    #####
     ###
      #
d=0.15 
###########
###     ###
###      ##
###     ###
###########

#---------- fUNCTION COUNT ---------------- 
#count occurences

count= function(str_var, char){ 

counter_char =  stri_count_regex(str_var, char)
freq_char= sum(counter_char)/length(str_var)

return (freq_char)

}


#-----------using sliding window over the symbols


a=SlidingWindow3("count", str_var, window, step, char='a')

b=SlidingWindow3("count", str_var, window, step, char='b')


#------- second part 


result=cbind(a,b)

cube_3=cube_6= array(dim = nrow(result))

#fonction de la distance euclidienne avec centroides 0.3;0.7 et 0.7;0.3
for (i in 1:nrow(result)) {
  cube_3[i]= min(sqrt((result[i,1]-0.3)^2 +(result[i,2]-0.7)^2),
      sqrt((result[i,1]-0.7)^2 +(result[i,2]-0.3)^2)
      )
 
 if(cube_3[i]>d)
 { cube_6[i]= TRUE}
 else{cube_6[i]=FALSE}
 
 
}
 
#result #contains the tuples of frequencies a and b
#cube_3 #contains the results of the euclidean distance
cube_6  #contains the booleans 





