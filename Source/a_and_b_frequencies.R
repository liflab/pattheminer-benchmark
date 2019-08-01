

#-- !!!!! you have to enter arguments window, step !!!!
#takes as input a file of 'a' and 'b' and returns the frequency of each over a window and with a step

##########################3 list of packages to use

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
str_var= read.csv(args[1], header= FALSE, stringsAsFactors = FALSE)

#str_var= read.csv("char_file.csv", header= TRUE)


str_var=unlist(str_var)        #use unlist to transform data from datafrane to numerical vectors
str_var= as.character(str_var) # to  change the type of data from 'Factor' to char

window= as.integer(args[2]) # transform args from data frame to integer
step= as.integer(args[3])


#---------- generate random strings ----------


#str_var = stri_rand_strings(100, 1, pattern = "[a b]")
#write.csv(str_var, file='char_file.csv', row.names=FALSE)


#---------- fUNCTION COUNT ---------------- 
#count occurences

count= function(str_var, char){ 

counter_char =  stri_count_regex(str_var, char)
freq_char= sum(counter_char)/length(str_var)

return (freq_char)

}


#-----------using sliding window over the symbols


print("frequency vector for a")
SlidingWindow3("count", str_var, window, step, char='a')

print("frequency vector for b")
SlidingWindow3("count", str_var, window, step, char='b')
