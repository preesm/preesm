/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
#ifndef ACTOR_H
#define ACTOR_H

void Camera_camera(i32 WIDTH, i32 HEIGHT, u8 *frame);
void Display_display(i32 WIDTH, i32 HEIGHT, u8 *frame);
void MergeImage_split(i32 NB_SLICES, i32 WIDTH, i32 HEIGHT, u8 *slices_in, u8 *frame);
void SeparateY_separate(i32 WIDTH, i32 HEIGHT, u8 *frame, u8 *Y);
void SobelFilter_filter(i32 NB_SLICES, i32 WIDTH, i32 HEIGHT, u8 *slice_in, u8 *slice_out);
void SplitImage_split(i32 NB_SLICES, i32 WIDTH, i32 HEIGHT, u8 *frame, u8 *slices_out);

#endif//ACTOR_H
