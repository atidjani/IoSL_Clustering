FROM base/archlinux

#Init Image
RUN pacman -Sy archlinux-keyring --noconfirm
RUN pacman -Syu --noconfirm
RUN pacman-db-upgrade
RUN rm /etc/pacman.d/mirrorlist
RUN sed -i 's/^#Server/Server/' /etc/pacman.d/mirrorlist.pacnew
RUN mv /etc/pacman.d/mirrorlist.pacnew /etc/pacman.d/mirrorlist


#Install required software
RUN pacman -S python python-django python-psycopg2 --noconfirm

# Set env variables used in this Dockerfile (add a unique prefix, such as DOCKYARD)
# Local directory with project source
ENV DOCKYARD_SRC=.
# Directory in container for all project files
ENV DOCKYARD_SRVHOME=/srv
# Directory in container for project source files
ENV DOCKYARD_SRVPROJ=/srv/visualizer

# Copy application source code to SRCDIR
COPY $DOCKYARD_SRC $DOCKYARD_SRVPROJ

#TODO: Compile the c++ code

# Copy entrypoint script into the image
WORKDIR $DOCKYARD_SRVPROJ
