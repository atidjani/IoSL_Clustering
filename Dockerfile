FROM base/archlinux

#Init Image
RUN pacman -Sy archlinux-keyring --noconfirm
RUN pacman -Syu --noconfirm
RUN pacman-db-upgrade
RUN rm /etc/pacman.d/mirrorlist
RUN sed -i 's/^#Server/Server/' /etc/pacman.d/mirrorlist.pacnew
RUN mv /etc/pacman.d/mirrorlist.pacnew /etc/pacman.d/mirrorlist


#Install required software
RUN pacman -Sy python2 python2-pip python2-django python2-psycopg2 eigen2 gcc make cmake r wget --noconfirm

#Install other python libraries
RUN pip2 install numpy
RUN pip2 install hcluster

#For production
# Set env variables used in this Dockerfile (add a unique prefix, such as DOCKYARD)
# Local directory with project source
ENV DOCKYARD_SRC=.
# Directory in container for all project files
ENV DOCKYARD_SRVHOME=/srv
# Directory in container for project source files
ENV DOCKYARD_SRVPROJ=/srv/visualizer

# Copy application source code to SRCDIR
# Self-tuning spectral clustering
COPY $DOCKYARD_SRC/STSC/cpp $DOCKYARD_SRVPROJ/STSC/cpp
# OPTICS Python
COPY $DOCKYARD_SRC/OPTICS/Python $DOCKYARD_SRVPROJ/OPTICS/Python
COPY $DOCKYARD_SRC/OPTICS/R $DOCKYARD_SRVPROJ/OPTICS/R

# Compile
WORKDIR $DOCKYARD_SRVPROJ/STSC/cpp/build
RUN cmake ../lib
RUN make
WORKDIR $DOCKYARD_SRVPROJ/STSC/cpp/runner/build
RUN cmake ..
RUN make

# Install R packages
WORKDIR $DOCKYARD_SRVPROJ/OPTICS/R
RUN wget http://cran.us.r-project.org/src/contrib/Rcpp_0.12.5.tar.gz
RUN R CMD INSTALL Rcpp_0.12.5.tar.gz
RUN wget http://cran.us.r-project.org/src/contrib/dbscan_0.9-7.tar.gz
RUN R CMD INSTALL dbscan_0.9-7.tar.gz

# Entrypoint script
COPY $DOCKYARD_SRC/docker-entrypoint.sh $DOCKYARD_SRVPROJ/docker-entrypoint.sh
RUN chmod 755 $DOCKYARD_SRVPROJ/docker-entrypoint.sh
# Visualizer -- For development mounted as volume
#COPY $DOCKYARD_SRC/Visualizer $DOCKYARD_SRVPROJ/Visualizer

WORKDIR $DOCKYARD_SRVPROJ
