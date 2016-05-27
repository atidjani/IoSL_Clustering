FROM base/archlinux

#Init Image
RUN pacman -Sy archlinux-keyring --noconfirm
RUN pacman -Syu --noconfirm
RUN pacman-db-upgrade
RUN rm /etc/pacman.d/mirrorlist
RUN sed -i 's/^#Server/Server/' /etc/pacman.d/mirrorlist.pacnew
RUN mv /etc/pacman.d/mirrorlist.pacnew /etc/pacman.d/mirrorlist


#Install required software
RUN pacman -S python python-pip python-django python-psycopg2 eigen2 gcc make cmake --noconfirm

#Install django-crontab
RUN pip install django-crontab

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
# Compile
WORKDIR $DOCKYARD_SRVPROJ/STSC/cpp/build
RUN cmake ../lib
RUN make
WORKDIR $DOCKYARD_SRVPROJ/STSC/cpp/runner/build
RUN cmake ..
RUN make

# Entrypoint script
COPY $DOCKYARD_SRC/docker-entrypoint.sh $DOCKYARD_SRVPROJ/docker-entrypoint.sh
# Visualizer -- For development mounted as volume
#COPY $DOCKYARD_SRC/Visualizer $DOCKYARD_SRVPROJ/Visualizer

# Copy entrypoint script into the image
WORKDIR $DOCKYARD_SRVPROJ
